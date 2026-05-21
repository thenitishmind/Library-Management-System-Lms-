package com.library.lms.service.impl;

import com.library.lms.dto.request.FinePaymentRequest;
import com.library.lms.dto.response.FineResponse;
import com.library.lms.dto.response.PagedResponse;
import com.library.lms.entity.Fine;
import com.library.lms.entity.Staff;
import com.library.lms.entity.enums.FineStatus;
import com.library.lms.exception.BusinessException;
import com.library.lms.exception.ResourceNotFoundException;
import com.library.lms.repository.FineRepository;
import com.library.lms.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FineServiceImpl {

    private final FineRepository fineRepository;
    private final StaffRepository staffRepository;

    @Transactional(readOnly = true)
    public PagedResponse<FineResponse> getFines(Long memberId, FineStatus status, Pageable pageable) {
        Page<Fine> page;
        if (memberId != null && status != null) {
            page = fineRepository.findByMemberIdAndStatus(memberId, status, pageable);
        } else if (memberId != null) {
            page = fineRepository.findByMemberId(memberId, pageable);
        } else if (status != null) {
            page = fineRepository.findByStatus(status, pageable);
        } else {
            page = fineRepository.findAll(pageable);
        }
        return PagedResponse.<FineResponse>builder()
            .content(page.getContent().stream().map(this::toResponse).collect(Collectors.toList()))
            .page(page.getNumber()).size(page.getSize())
            .totalElements(page.getTotalElements()).totalPages(page.getTotalPages())
            .last(page.isLast()).build();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_MEMBER','ROLE_LIBRARIAN','ROLE_ADMIN')")
    public FineResponse payFine(Long fineId, FinePaymentRequest request) {
        Fine fine = findOrThrow(fineId);

        if (fine.getStatus() == FineStatus.PAID || fine.getStatus() == FineStatus.WAIVED) {
            throw new BusinessException("Fine is already " + fine.getStatus());
        }

        BigDecimal remaining = fine.getAmount().subtract(fine.getPaidAmount());
        if (request.getAmount().compareTo(remaining) > 0) {
            throw new BusinessException("Payment exceeds outstanding amount: " + remaining);
        }

        fine.setPaidAmount(fine.getPaidAmount().add(request.getAmount()));

        if (fine.getPaidAmount().compareTo(fine.getAmount()) >= 0) {
            fine.setStatus(FineStatus.PAID);
            fine.setPaidAt(LocalDateTime.now());
        } else {
            fine.setStatus(FineStatus.PARTIAL);
        }

        Fine savedFine = fineRepository.save(fine);
        log.info("Payment of {} applied to fine id={}", request.getAmount(), fineId);
        return toResponse(savedFine);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public FineResponse waiveFine(Long fineId) {
        Fine fine = findOrThrow(fineId);
        if (fine.getStatus() == FineStatus.PAID || fine.getStatus() == FineStatus.WAIVED) {
            throw new BusinessException("Fine already " + fine.getStatus());
        }

        Staff waivedBy = staffRepository.findByEmail(
            SecurityContextHolder.getContext().getAuthentication().getName()
        ).orElse(null);

        fine.setStatus(FineStatus.WAIVED);
        fine.setWaivedBy(waivedBy);
        fine.setPaidAt(LocalDateTime.now());
        Fine savedFine = fineRepository.save(fine);
        log.info("Waived fine id={}", fineId);
        return toResponse(savedFine);
    }

    private Fine findOrThrow(Long id) {
        return fineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fine", id));
    }

    public FineResponse toResponse(Fine fine) {
        FineResponse resp = new FineResponse();
        resp.setId(fine.getId());
        resp.setMemberId(fine.getMember().getId());
        resp.setMemberName(fine.getMember().getFirstName() + " " + fine.getMember().getLastName());
        if (fine.getLoan() != null) resp.setLoanId(fine.getLoan().getId());
        resp.setFineType(fine.getFineType());
        resp.setAmount(fine.getAmount());
        resp.setPaidAmount(fine.getPaidAmount());
        resp.setOutstanding(fine.getAmount().subtract(fine.getPaidAmount()));
        resp.setStatus(fine.getStatus());
        resp.setIssuedAt(fine.getIssuedAt());
        resp.setPaidAt(fine.getPaidAt());
        return resp;
    }
}
