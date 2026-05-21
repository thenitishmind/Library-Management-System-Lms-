package com.library.lms.dto.request;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoanRequest {

    @NotNull
    private Long memberId;

    @NotNull
    private Long bookCopyId;

    private String notes;
}
