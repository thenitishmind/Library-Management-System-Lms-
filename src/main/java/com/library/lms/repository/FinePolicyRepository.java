package com.library.lms.repository;

import com.library.lms.entity.FinePolicy;
import com.library.lms.entity.enums.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinePolicyRepository extends JpaRepository<FinePolicy, Long> {

    Optional<FinePolicy> findByMembershipType(MembershipType membershipType);
}
