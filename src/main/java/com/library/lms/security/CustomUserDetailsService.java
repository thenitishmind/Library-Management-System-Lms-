package com.library.lms.security;

import com.library.lms.entity.Member;
import com.library.lms.entity.Staff;
import com.library.lms.repository.MemberRepository;
import com.library.lms.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final StaffRepository staffRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try staff first, then members
        return staffRepository.findByEmail(email)
            .map(this::buildStaffDetails)
            .orElseGet(() -> memberRepository.findByEmail(email)
                .map(this::buildMemberDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email)));
    }

    private UserDetails buildStaffDetails(Staff staff) {
        List<SimpleGrantedAuthority> authorities = buildAuthorities(staff.getRole());
        return User.builder()
            .username(staff.getEmail())
            .password(staff.getPasswordHash())
            .authorities(authorities)
            .accountLocked(!staff.isActive())
            .build();
    }

    private UserDetails buildMemberDetails(Member member) {
        List<SimpleGrantedAuthority> authorities = buildAuthorities(member.getRole());
        boolean locked = member.getStatus() == com.library.lms.entity.enums.MemberStatus.SUSPENDED;
        return User.builder()
            .username(member.getEmail())
            .password(member.getPasswordHash())
            .authorities(authorities)
            .accountLocked(locked)
            .build();
    }

    private List<SimpleGrantedAuthority> buildAuthorities(com.library.lms.entity.Role role) {
        if (role == null) return java.util.Collections.emptyList();
        Stream<SimpleGrantedAuthority> roleAuth = Stream.of(new SimpleGrantedAuthority(role.getName()));
        Stream<SimpleGrantedAuthority> permAuth = role.getPermissions().stream()
            .map(p -> new SimpleGrantedAuthority(p.getName()));
        return Stream.concat(roleAuth, permAuth).collect(Collectors.toList());
    }
}
