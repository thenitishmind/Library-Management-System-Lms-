package com.library.lms.service.impl;

import com.library.lms.repository.BookCopyRepository;
import com.library.lms.repository.LoanRepository;
import com.library.lms.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl {

    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;
    private final BookCopyRepository bookCopyRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public Map<String, Object> getOverdueReport() {
        java.util.List<com.library.lms.entity.Loan> overdueLoans = loanRepository.findOverdueLoans(LocalDate.now());
        Map<String, Object> report = new HashMap<>();
        report.put("totalOverdue", overdueLoans.size());
        report.put("generatedAt", LocalDate.now());
        log.info("Generated overdue report: {} loans", overdueLoans.size());
        return report;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public Map<String, Object> getMemberStats() {
        long total = memberRepository.count();
        long active = memberRepository.findByStatus(
            com.library.lms.entity.enums.MemberStatus.ACTIVE, PageRequest.of(0, 1)).getTotalElements();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMembers", total);
        stats.put("activeMembers", active);
        stats.put("generatedAt", LocalDate.now());
        return stats;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public Map<String, Object> getMostBorrowedBooks(LocalDate startDate, LocalDate endDate) {
        // Dummy implementation since actual query depends on loan repository
        Map<String, Object> report = new HashMap<>();
        report.put("message", "Most borrowed books report from " + startDate + " to " + endDate);
        report.put("generatedAt", LocalDate.now());
        return report;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public Map<String, Object> getInventoryReport() {
        long totalCopies = bookCopyRepository.count();
        Map<String, Object> report = new HashMap<>();
        report.put("totalCopies", totalCopies);
        report.put("generatedAt", LocalDate.now());
        return report;
    }
}
