package com.library.lms.dto.response;

import com.library.lms.entity.enums.LoanStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LoanResponse {
    private Long id;
    private Long memberId;
    private String memberName;
    private Long bookCopyId;
    private String bookTitle;
    private String barcode;
    private LocalDateTime issueDate;
    private LocalDate dueDate;
    private LocalDateTime returnDate;
    private LoanStatus status;
    private Integer renewalCount;
    private boolean overdue;
    private long overdueDays;
    private String notes;
}
