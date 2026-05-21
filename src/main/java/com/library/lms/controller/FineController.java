package com.library.lms.controller;

import com.library.lms.dto.request.FinePaymentRequest;
import com.library.lms.dto.response.FineResponse;
import com.library.lms.dto.response.PagedResponse;
import com.library.lms.entity.enums.FineStatus;
import com.library.lms.service.impl.FineServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fines")
@RequiredArgsConstructor
@Tag(name = "Fines", description = "Fine management")
public class FineController {

    private final FineServiceImpl fineService;

    @GetMapping
    @Operation(summary = "Get fines with optional filters")
    public ResponseEntity<PagedResponse<FineResponse>> getFines(
        @RequestParam(required = false) Long member,
        @RequestParam(required = false) FineStatus status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(fineService.getFines(member, status, PageRequest.of(page, size)));
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "Pay a fine")
    public ResponseEntity<FineResponse> payFine(
        @PathVariable Long id,
        @Valid @RequestBody FinePaymentRequest request
    ) {
        return ResponseEntity.ok(fineService.payFine(id, request));
    }

    @PostMapping("/{id}/waive")
    @Operation(summary = "Waive a fine (librarian only)")
    public ResponseEntity<FineResponse> waiveFine(@PathVariable Long id) {
        return ResponseEntity.ok(fineService.waiveFine(id));
    }
}
