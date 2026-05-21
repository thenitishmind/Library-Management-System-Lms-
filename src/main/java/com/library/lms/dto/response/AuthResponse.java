package com.library.lms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private UserSummary user;

    @Data
    @Builder
    public static class UserSummary {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private String role;
        private String userType;
    }
}
