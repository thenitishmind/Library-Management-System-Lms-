package com.library.lms.dto.request;

import javax.validation.constraints.*;
import lombok.Data;

import java.util.Set;

@Data
public class BookRequest {

    @NotBlank @Size(max = 20)
    private String isbn;

    @NotBlank @Size(max = 500)
    private String title;

    @Size(max = 300)
    private String subtitle;

    @Size(max = 10)
    private String language;

    @Size(max = 50)
    private String edition;

    @Min(1000) @Max(9999)
    private Integer publicationYear;

    @Min(1)
    private Integer totalPages;

    private String description;

    @Size(max = 500)
    private String coverImageUrl;

    private Long publisherId;

    private Set<Long> authorIds;

    private Set<Long> categoryIds;
}
