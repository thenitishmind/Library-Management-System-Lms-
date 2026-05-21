package com.library.lms.entity;

import com.library.lms.entity.enums.ReservationStatus;
import javax.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reservation {

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

    @Column(name = "reservation_date", nullable = false)
    @Builder.Default
    private LocalDateTime reservationDate = LocalDateTime.now();

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "queue_position")
    private Integer queuePosition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.PENDING;

    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;
}
