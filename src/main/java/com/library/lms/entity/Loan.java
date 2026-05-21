package com.library.lms.entity;

import com.library.lms.entity.enums.LoanStatus;
import javax.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "loans", indexes = {
    @Index(columnList = "member_id,status"),
    @Index(columnList = "due_date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @ToString.Exclude
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_id", nullable = false)
    private BookCopy bookCopy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by")
    private Staff issuedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "returned_to")
    private Staff returnedTo;

    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LoanStatus status = LoanStatus.ACTIVE;

    @Column(name = "renewal_count")
    @Builder.Default
    private Integer renewalCount = 0;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToOne(mappedBy = "loan", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Fine fine;

    public boolean isOverdue() {
        return status == LoanStatus.ACTIVE && LocalDate.now().isAfter(dueDate);
    }

    public long overdueDays() {
        if (!isOverdue()) return 0;
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
}
