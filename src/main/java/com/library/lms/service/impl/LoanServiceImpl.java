package com.library.lms.service.impl;

import com.library.lms.dto.request.LoanRequest;
import com.library.lms.dto.response.LoanResponse;
import com.library.lms.dto.response.PagedResponse;
import com.library.lms.entity.*;
import com.library.lms.entity.enums.*;
import com.library.lms.exception.BusinessException;
import com.library.lms.exception.ResourceNotFoundException;
import com.library.lms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanServiceImpl {

    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;
    private final BookCopyRepository bookCopyRepository;
    private final StaffRepository staffRepository;
    private final FineRepository fineRepository;
    private final FinePolicyRepository finePolicyRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public LoanResponse issueLoan(LoanRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
            .orElseThrow(() -> new ResourceNotFoundException("Member", request.getMemberId()));
        BookCopy copy = bookCopyRepository.findById(request.getBookCopyId())
            .orElseThrow(() -> new ResourceNotFoundException("BookCopy", request.getBookCopyId()));

        // Validations
        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new BusinessException("Member account is not active");
        }
        if (member.getMembershipExpiry() != null && member.getMembershipExpiry().isBefore(LocalDate.now())) {
            throw new BusinessException("Member membership has expired");
        }
        if (copy.getStatus() != BookCopyStatus.AVAILABLE) {
            throw new BusinessException("Book copy is not available: " + copy.getBarcode());
        }

        BigDecimal unpaidFines = fineRepository.sumUnpaidByMember(member.getId());
        if (unpaidFines.compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("Member has unpaid fines: " + unpaidFines);
        }

        FinePolicy policy = finePolicyRepository.findByMembershipType(member.getMembershipType())
            .orElseThrow(() -> new BusinessException("No fine policy for: " + member.getMembershipType()));

        long activeLoans = loanRepository.countActiveLoansByMember(member.getId());
        if (activeLoans >= policy.getMaxActiveLoans()) {
            throw new BusinessException("Member has reached max active loans limit: " + policy.getMaxActiveLoans());
        }

        Staff issuedBy = resolveStaff();
        LocalDate dueDate = LocalDate.now().plusDays(policy.getMaxLoanDays());

        Loan loan = Loan.builder()
            .member(member)
            .bookCopy(copy)
            .issuedBy(issuedBy)
            .issueDate(LocalDateTime.now())
            .dueDate(dueDate)
            .status(LoanStatus.ACTIVE)
            .renewalCount(0)
            .notes(request.getNotes())
            .build();

        copy.setStatus(BookCopyStatus.BORROWED);
        bookCopyRepository.save(copy);
        Loan savedLoan = loanRepository.save(loan);
        log.info("Issued loan id={} to member id={}", savedLoan.getId(), member.getId());
        return toResponse(savedLoan);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public LoanResponse returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new ResourceNotFoundException("Loan", loanId));

        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new BusinessException("Loan already returned");
        }

        loan.setReturnDate(LocalDateTime.now());
        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnedTo(resolveStaff());

        BookCopy copy = loan.getBookCopy();
        copy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopyRepository.save(copy);

        if (loan.isOverdue()) {
            createOverdueFine(loan);
        }

        Loan savedLoan = loanRepository.save(loan);

        // Notify next in queue
        List<Reservation> nextInQueue = reservationRepository.findNextInQueue(copy.getBook().getId());
        if (!nextInQueue.isEmpty()) {
            Reservation reservation = nextInQueue.get(0);
            reservation.setStatus(ReservationStatus.READY);
            reservation.setNotifiedAt(LocalDateTime.now());
            reservation.setExpiryDate(LocalDateTime.now().plusDays(3));
            Reservation savedReservation = reservationRepository.save(reservation);
            copy.setStatus(BookCopyStatus.RESERVED);
            bookCopyRepository.save(copy);
            log.info("Notified reservation id={} that book is ready", savedReservation.getId());
        }

        log.info("Returned loan id={}", loanId);
        return toResponse(savedLoan);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN','ROLE_MEMBER')")
    public LoanResponse renewLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new ResourceNotFoundException("Loan", loanId));

        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new BusinessException("Only active loans can be renewed");
        }

        FinePolicy policy = finePolicyRepository.findByMembershipType(loan.getMember().getMembershipType())
            .orElseThrow(() -> new BusinessException("Policy not found"));

        if (loan.getRenewalCount() >= policy.getMaxRenewals()) {
            throw new BusinessException("Maximum renewals reached: " + policy.getMaxRenewals());
        }

        boolean hasReservation = !reservationRepository
            .findQueueForBook(loan.getBookCopy().getBook().getId()).isEmpty();
        if (hasReservation) {
            throw new BusinessException("Cannot renew: book has pending reservations");
        }

        loan.setDueDate(loan.getDueDate().plusDays(policy.getMaxLoanDays()));
        loan.setRenewalCount(loan.getRenewalCount() + 1);
        Loan savedLoan = loanRepository.save(loan);
        log.info("Renewed loan id={}, renewal #{}", loanId, savedLoan.getRenewalCount());
        return toResponse(savedLoan);
    }

    @Transactional(readOnly = true)
    public PagedResponse<LoanResponse> getLoans(Long memberId, LoanStatus status, Pageable pageable) {
        Page<Loan> page;
        if (memberId != null && status != null) {
            page = loanRepository.findByMemberIdAndStatus(memberId, status, pageable);
        } else if (memberId != null) {
            page = loanRepository.findByMemberId(memberId, pageable);
        } else if (status != null) {
            page = loanRepository.findByStatus(status, pageable);
        } else {
            page = loanRepository.findAll(pageable);
        }
        return PagedResponse.<LoanResponse>builder()
            .content(page.getContent().stream().map(this::toResponse).collect(Collectors.toList()))
            .page(page.getNumber()).size(page.getSize())
            .totalElements(page.getTotalElements()).totalPages(page.getTotalPages())
            .last(page.isLast()).build();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public List<LoanResponse> getOverdueLoans() {
        return loanRepository.findOverdueLoans(LocalDate.now())
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private void createOverdueFine(Loan loan) {
        if (loan.getFine() != null) return;
        FinePolicy policy = finePolicyRepository
            .findByMembershipType(loan.getMember().getMembershipType()).orElse(null);
        if (policy == null) return;

        long days = Math.max(0, loan.overdueDays() - policy.getGracePeriodDays());
        if (days <= 0) return;

        BigDecimal amount = policy.getDailyRate()
            .multiply(BigDecimal.valueOf(days))
            .min(policy.getMaxFine());

        Fine fine = Fine.builder()
            .member(loan.getMember())
            .loan(loan)
            .fineType(FineType.OVERDUE)
            .amount(amount)
            .paidAmount(BigDecimal.ZERO)
            .status(FineStatus.PENDING)
            .build();
        fineRepository.save(fine);
        log.info("Created overdue fine of {} for loan id={}", amount, loan.getId());
    }

    private Staff resolveStaff() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return staffRepository.findByEmail(auth.getName()).orElse(null);
        }
        return null;
    }

    public LoanResponse toResponse(Loan loan) {
        LoanResponse resp = new LoanResponse();
        resp.setId(loan.getId());
        resp.setMemberId(loan.getMember().getId());
        resp.setMemberName(loan.getMember().getFirstName() + " " + loan.getMember().getLastName());
        resp.setBookCopyId(loan.getBookCopy().getId());
        resp.setBookTitle(loan.getBookCopy().getBook().getTitle());
        resp.setBarcode(loan.getBookCopy().getBarcode());
        resp.setIssueDate(loan.getIssueDate());
        resp.setDueDate(loan.getDueDate());
        resp.setReturnDate(loan.getReturnDate());
        resp.setStatus(loan.getStatus());
        resp.setRenewalCount(loan.getRenewalCount());
        resp.setOverdue(loan.isOverdue());
        resp.setOverdueDays(loan.overdueDays());
        resp.setNotes(loan.getNotes());
        return resp;
    }
}
