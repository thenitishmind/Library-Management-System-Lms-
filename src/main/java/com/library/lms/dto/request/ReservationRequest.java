package com.library.lms.dto.request;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationRequest {

    @NotNull
    private Long memberId;

    @NotNull
    private Long bookId;
}
