package com.library.lms.dto.request;

import javax.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {

    @NotNull
    private Long bookId;

    @NotNull @Min(1) @Max(5)
    private Short rating;

    private String reviewText;
}
