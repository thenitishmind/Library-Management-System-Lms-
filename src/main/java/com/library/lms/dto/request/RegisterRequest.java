package com.library.lms.dto.request;

import com.library.lms.entity.enums.MembershipType;
import javax.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {

    @NotBlank @Size(max = 100)
    private String firstName;

    @NotBlank @Size(max = 100)
    private String lastName;

    @NotBlank @Email @Size(max = 150)
    private String email;

    @NotBlank @Size(min = 8, max = 100)
    private String password;

    @Size(max = 20)
    private String phone;

    private LocalDate dateOfBirth;

    private MembershipType membershipType = MembershipType.STANDARD;
}
