package com.library.lms.repository;

import com.library.lms.entity.Fine;
import com.library.lms.entity.enums.FineStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {

    @Query("SELECT f FROM Fine f WHERE f.member.id = :memberId AND f.status IN ('PENDING','PARTIAL')")
    List<Fine> findUnpaidFinesByMember(@Param("memberId") Long memberId);

    @Query("SELECT COALESCE(SUM(f.amount - f.paidAmount), 0) FROM Fine f WHERE f.member.id = :memberId AND f.status IN ('PENDING','PARTIAL')")
    BigDecimal sumUnpaidByMember(@Param("memberId") Long memberId);

    Page<Fine> findByMemberId(Long memberId, Pageable pageable);

    Page<Fine> findByMemberIdAndStatus(Long memberId, FineStatus status, Pageable pageable);

    Page<Fine> findByStatus(FineStatus status, Pageable pageable);
}
