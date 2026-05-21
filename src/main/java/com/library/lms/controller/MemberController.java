package com.library.lms.controller;

import com.library.lms.dto.request.RegisterRequest;
import com.library.lms.dto.response.MemberResponse;
import com.library.lms.dto.response.PagedResponse;
import com.library.lms.service.impl.MemberServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "Member management")
public class MemberController {

    private final MemberServiceImpl memberService;

    @GetMapping
    @Operation(summary = "Get all members")
    public ResponseEntity<PagedResponse<MemberResponse>> getAllMembers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(memberService.getAllMembers(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get member by ID")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @PostMapping
    @Operation(summary = "Register a new member")
    public ResponseEntity<MemberResponse> registerMember(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.registerMember(request));
    }

    @PostMapping("/{id}/suspend")
    @Operation(summary = "Suspend a member")
    public ResponseEntity<MemberResponse> suspendMember(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.suspendMember(id));
    }

    @PostMapping("/{id}/renew-membership")
    @Operation(summary = "Renew membership")
    public ResponseEntity<MemberResponse> renewMembership(
        @PathVariable Long id,
        @RequestParam(defaultValue = "12") int months
    ) {
        return ResponseEntity.ok(memberService.renewMembership(id, months));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a member")
    public ResponseEntity<MemberResponse> updateMember(
        @PathVariable Long id,
        @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(memberService.updateMember(id, request));
    }

    @GetMapping("/{id}/dashboard")
    @Operation(summary = "Get member dashboard")
    public ResponseEntity<java.util.Map<String, Object>> getMemberDashboard(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberDashboard(id));
    }
}
