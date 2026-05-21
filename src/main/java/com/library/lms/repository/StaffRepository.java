package com.library.lms.repository;

import com.library.lms.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    Optional<Staff> findByEmail(String email);

    Optional<Staff> findByEmployeeCode(String employeeCode);

    boolean existsByEmail(String email);
}
