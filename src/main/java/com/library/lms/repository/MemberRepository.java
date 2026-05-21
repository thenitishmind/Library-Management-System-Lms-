package com.library.lms.repository;

import com.library.lms.entity.Member;
import com.library.lms.entity.enums.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByMemberCode(String memberCode);

    boolean existsByEmail(String email);

    @Query("SELECT m FROM Member m WHERE m.membershipExpiry < :today AND m.status = 'ACTIVE'")
    List<Member> findExpiredMemberships(@Param("today") LocalDate today);

    Page<Member> findByStatus(MemberStatus status, Pageable pageable);
}
