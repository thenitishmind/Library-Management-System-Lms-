package com.library.lms.repository;

import com.library.lms.entity.Loan;
import com.library.lms.entity.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("SELECT l FROM Loan l WHERE l.member.id = :memberId AND l.status = 'ACTIVE'")
    List<Loan> findActiveLoansByMember(@Param("memberId") Long memberId);

    @Query("SELECT l FROM Loan l WHERE l.status = 'ACTIVE' AND l.dueDate < :today")
    List<Loan> findOverdueLoans(@Param("today") LocalDate today);

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.member.id = :memberId AND l.status = 'ACTIVE'")
    long countActiveLoansByMember(@Param("memberId") Long memberId);

    Page<Loan> findByMemberIdAndStatus(Long memberId, LoanStatus status, Pageable pageable);

    Page<Loan> findByMemberId(Long memberId, Pageable pageable);

    Page<Loan> findByStatus(LoanStatus status, Pageable pageable);

    @Query("SELECT l FROM Loan l WHERE l.dueDate = :dueDate AND l.status = 'ACTIVE'")
    List<Loan> findLoansDueOn(@Param("dueDate") LocalDate dueDate);

    boolean existsByBookCopyIdAndStatus(Long bookCopyId, LoanStatus status);
}
