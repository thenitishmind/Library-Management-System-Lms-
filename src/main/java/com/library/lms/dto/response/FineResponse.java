package com.library.lms.dto.response;

import com.library.lms.entity.enums.FineStatus;
import com.library.lms.entity.enums.FineType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FineResponse {
    private Long id;
    private Long memberId;
    private String memberName;
    private Long loanId;
    private FineType fineType;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal outstanding;
    private FineStatus status;
    private LocalDateTime issuedAt;
    private LocalDateTime paidAt;
}
