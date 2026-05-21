package com.library.lms.service.impl;

import com.library.lms.dto.request.LoginRequest;
import com.library.lms.dto.request.RegisterRequest;
import com.library.lms.dto.response.AuthResponse;
import com.library.lms.entity.Member;
import com.library.lms.entity.Role;
import com.library.lms.entity.enums.MemberStatus;
import com.library.lms.exception.BusinessException;
import com.library.lms.repository.MemberRepository;
import com.library.lms.repository.RoleRepository;
import com.library.lms.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered: " + request.getEmail());
        }
        Role memberRole = roleRepository.findByName("ROLE_MEMBER")
            .orElseThrow(() -> new BusinessException("Default role not configured"));

        Member member = Member.builder()
            .memberCode(generateMemberCode())
            .email(request.getEmail())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .phone(request.getPhone())
            .dateOfBirth(request.getDateOfBirth())
            .membershipType(request.getMembershipType())
            .membershipExpiry(LocalDate.now().plusYears(1))
            .status(MemberStatus.ACTIVE)
            .role(memberRole)
            .build();

        member = memberRepository.save(member);
        log.info("Registered new member: {}", member.getEmail());

        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        return buildAuthResponse(auth, member);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        Member member = memberRepository.findByEmail(request.getEmail()).orElse(null);
        log.info("Login: {}", request.getEmail());
        return buildAuthResponse(auth, member);
    }

    private AuthResponse buildAuthResponse(Authentication auth, Member member) {
        String accessToken = tokenProvider.generateAccessToken(auth);
        String refreshToken = tokenProvider.generateRefreshToken(auth.getName());

        AuthResponse.UserSummary userSummary = null;
        if (member != null) {
            userSummary = AuthResponse.UserSummary.builder()
                .id(member.getId())
                .email(member.getEmail())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .role(member.getRole() != null ? member.getRole().getName() : null)
                .userType("MEMBER")
                .build();
        }

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(900)
            .user(userSummary)
            .build();
    }

    private String generateMemberCode() {
        return "MEM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
