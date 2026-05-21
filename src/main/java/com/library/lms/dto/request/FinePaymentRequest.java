package com.library.lms.dto.request;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FinePaymentRequest {

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
}
