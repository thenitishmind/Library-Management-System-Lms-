package com.library.lms.dto.response;

import com.library.lms.entity.enums.BookStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookResponse {
    private Long id;
    private String isbn;
    private String title;
    private String subtitle;
    private String language;
    private String edition;
    private Integer publicationYear;
    private Integer totalPages;
    private String description;
    private String coverImageUrl;
    private BookStatus status;
    private PublisherSummary publisher;
    private Set<AuthorSummary> authors;
    private Set<CategorySummary> categories;
    private long availableCopies;
    private long totalCopies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class PublisherSummary {
        private Long id;
        private String name;
    }

    @Data
    public static class AuthorSummary {
        private Long id;
        private String firstName;
        private String lastName;
    }

    @Data
    public static class CategorySummary {
        private Long id;
        private String name;
        private String slug;
    }
}
