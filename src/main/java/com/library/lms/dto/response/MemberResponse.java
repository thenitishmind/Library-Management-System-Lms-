package com.library.lms.dto.response;

import com.library.lms.entity.enums.MembershipType;
import com.library.lms.entity.enums.MemberStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MemberResponse {
    private Long id;
    private String memberCode;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDate dateOfBirth;
    private MembershipType membershipType;
    private LocalDate membershipExpiry;
    private MemberStatus status;
    private String roleName;
    private LocalDateTime createdAt;
}
