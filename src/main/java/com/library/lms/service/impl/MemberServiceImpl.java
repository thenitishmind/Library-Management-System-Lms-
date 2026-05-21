package com.library.lms.service.impl;

import com.library.lms.dto.request.RegisterRequest;
import com.library.lms.dto.response.MemberResponse;
import com.library.lms.dto.response.PagedResponse;
import com.library.lms.entity.Member;
import com.library.lms.entity.Role;
import com.library.lms.entity.enums.MemberStatus;
import com.library.lms.exception.BusinessException;
import com.library.lms.exception.ResourceNotFoundException;
import com.library.lms.repository.FineRepository;
import com.library.lms.repository.MemberRepository;
import com.library.lms.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final FineRepository fineRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public PagedResponse<MemberResponse> getAllMembers(Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return toPagedResponse(page);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public MemberResponse registerMember(RegisterRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered: " + request.getEmail());
        }
        Role role = roleRepository.findByName("ROLE_MEMBER")
            .orElseThrow(() -> new BusinessException("Default role not configured"));

        Member member = Member.builder()
            .memberCode("MEM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .email(request.getEmail())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .phone(request.getPhone())
            .dateOfBirth(request.getDateOfBirth())
            .membershipType(request.getMembershipType())
            .membershipExpiry(LocalDate.now().plusYears(1))
            .status(MemberStatus.ACTIVE)
            .role(role)
            .build();

        member = memberRepository.save(member);
        log.info("Registered member: {}", member.getEmail());
        return toResponse(member);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public MemberResponse suspendMember(Long id) {
        Member member = findOrThrow(id);
        BigDecimal outstanding = fineRepository.sumUnpaidByMember(id);
        member.setStatus(MemberStatus.SUSPENDED);
        memberRepository.save(member);
        log.info("Suspended member id={}, outstanding fines={}", id, outstanding);
        return toResponse(member);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public MemberResponse renewMembership(Long id, int months) {
        Member member = findOrThrow(id);
        LocalDate base = member.getMembershipExpiry() != null && member.getMembershipExpiry().isAfter(LocalDate.now())
            ? member.getMembershipExpiry()
            : LocalDate.now();
        member.setMembershipExpiry(base.plusMonths(months));
        member.setStatus(MemberStatus.ACTIVE);
        memberRepository.save(member);
        log.info("Renewed membership for member id={}", id);
        return toResponse(member);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public MemberResponse updateMember(Long id, com.library.lms.dto.request.RegisterRequest request) {
        Member member = findOrThrow(id);
        if (!member.getEmail().equals(request.getEmail()) && memberRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered: " + request.getEmail());
        }
        member.setEmail(request.getEmail());
        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setPhone(request.getPhone());
        member.setDateOfBirth(request.getDateOfBirth());
        memberRepository.save(member);
        log.info("Updated member id={}", id);
        return toResponse(member);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_MEMBER') and #id == authentication.principal.id or hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public java.util.Map<String, Object> getMemberDashboard(Long id) {
        Member member = findOrThrow(id);
        java.util.Map<String, Object> dashboard = new java.util.HashMap<>();
        dashboard.put("memberInfo", toResponse(member));
        dashboard.put("outstandingFines", fineRepository.sumUnpaidByMember(id));
        return dashboard;
    }

    private Member findOrThrow(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Member", id));
    }

    public MemberResponse toResponse(Member m) {
        MemberResponse resp = new MemberResponse();
        resp.setId(m.getId());
        resp.setMemberCode(m.getMemberCode());
        resp.setEmail(m.getEmail());
        resp.setFirstName(m.getFirstName());
        resp.setLastName(m.getLastName());
        resp.setPhone(m.getPhone());
        resp.setDateOfBirth(m.getDateOfBirth());
        resp.setMembershipType(m.getMembershipType());
        resp.setMembershipExpiry(m.getMembershipExpiry());
        resp.setStatus(m.getStatus());
        resp.setRoleName(m.getRole() != null ? m.getRole().getName() : null);
        resp.setCreatedAt(m.getCreatedAt());
        return resp;
    }

    private PagedResponse<MemberResponse> toPagedResponse(Page<Member> page) {
        return PagedResponse.<MemberResponse>builder()
            .content(page.getContent().stream().map(this::toResponse).collect(Collectors.toList()))
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }
}
