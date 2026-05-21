package com.library.lms.entity;

import com.library.lms.entity.enums.ReviewStatus;
import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews",
    uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "book_id"}),
    indexes = @Index(columnList = "member_id,book_id")
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @ToString.Exclude
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    @Min(1) @Max(5)
    private Short rating;

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.PENDING;

    @Column(name = "reviewed_at", nullable = false)
    @Builder.Default
    private LocalDateTime reviewedAt = LocalDateTime.now();
}
