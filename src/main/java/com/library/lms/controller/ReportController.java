package com.library.lms.controller;

import com.library.lms.service.impl.ReportServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Reporting endpoints")
public class ReportController {

    private final ReportServiceImpl reportService;

    @GetMapping("/overdue")
    @Operation(summary = "Overdue loans report")
    public ResponseEntity<Map<String, Object>> overdueReport() {
        return ResponseEntity.ok(reportService.getOverdueReport());
    }

    @GetMapping("/member-stats")
    @Operation(summary = "Member statistics")
    public ResponseEntity<Map<String, Object>> memberStats() {
        return ResponseEntity.ok(reportService.getMemberStats());
    }

    @GetMapping("/inventory")
    @Operation(summary = "Inventory report")
    public ResponseEntity<Map<String, Object>> inventoryReport() {
        return ResponseEntity.ok(reportService.getInventoryReport());
    }

    @GetMapping("/popular-books")
    @Operation(summary = "Most borrowed books report")
    public ResponseEntity<Map<String, Object>> popularBooks(
        @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
        @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate
    ) {
        if (startDate == null) startDate = java.time.LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = java.time.LocalDate.now();
        return ResponseEntity.ok(reportService.getMostBorrowedBooks(startDate, endDate));
    }
}
