package com.library.lms.controller;

import com.library.lms.dto.request.LoanRequest;
import com.library.lms.dto.response.LoanResponse;
import com.library.lms.dto.response.PagedResponse;
import com.library.lms.entity.enums.LoanStatus;
import com.library.lms.service.impl.LoanServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Loan management")
public class LoanController {

    private final LoanServiceImpl loanService;

    @PostMapping
    @Operation(summary = "Issue a loan")
    public ResponseEntity<LoanResponse> issueLoan(@Valid @RequestBody LoanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.issueLoan(request));
    }

    @PutMapping("/{id}/return")
    @Operation(summary = "Return a book")
    public ResponseEntity<LoanResponse> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.returnBook(id));
    }

    @PutMapping("/{id}/renew")
    @Operation(summary = "Renew a loan")
    public ResponseEntity<LoanResponse> renewLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.renewLoan(id));
    }

    @GetMapping
    @Operation(summary = "Get loans with optional filters")
    public ResponseEntity<PagedResponse<LoanResponse>> getLoans(
        @RequestParam(required = false) Long member,
        @RequestParam(required = false) LoanStatus status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(loanService.getLoans(member, status, PageRequest.of(page, size)));
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue loans")
    public ResponseEntity<List<LoanResponse>> getOverdueLoans() {
        return ResponseEntity.ok(loanService.getOverdueLoans());
    }
}
