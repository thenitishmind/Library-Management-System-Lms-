package com.library.lms.entity;

import com.library.lms.entity.enums.BookCopyCondition;
import com.library.lms.entity.enums.BookCopyStatus;
import javax.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book_copies", indexes = {
    @Index(columnList = "book_id,status"),
    @Index(columnList = "barcode")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String barcode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @ToString.Exclude
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_id")
    private Shelf shelf;

    @Enumerated(EnumType.STRING)
    @Column(name = "`condition`", nullable = false)
    @Builder.Default
    private BookCopyCondition condition = BookCopyCondition.GOOD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookCopyStatus status = BookCopyStatus.AVAILABLE;

    @Column(name = "acquisition_date")
    private LocalDate acquisitionDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "bookCopy")
    @Builder.Default
    @ToString.Exclude
    private List<Loan> loans = new ArrayList<>();
}
