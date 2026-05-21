package com.library.lms.repository;

import com.library.lms.entity.Reservation;
import com.library.lms.entity.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByMemberIdAndStatus(Long memberId, ReservationStatus status);

    @Query("SELECT r FROM Reservation r " +
           "WHERE r.book.id = :bookId AND r.status IN ('PENDING','READY') " +
           "ORDER BY r.queuePosition ASC")
    List<Reservation> findQueueForBook(@Param("bookId") Long bookId);

    @Query("SELECT r FROM Reservation r " +
           "WHERE r.book.id = :bookId AND r.status IN ('PENDING','READY') " +
           "ORDER BY r.queuePosition ASC")
    List<Reservation> findNextInQueue(@Param("bookId") Long bookId);

    Optional<Reservation> findByMemberIdAndBookIdAndStatusIn(
        Long memberId, Long bookId, List<ReservationStatus> statuses);

    boolean existsByMemberIdAndBookIdAndStatusIn(
        Long memberId, Long bookId, List<ReservationStatus> statuses);

    @Query("SELECT COALESCE(MAX(r.queuePosition), 0) FROM Reservation r WHERE r.book.id = :bookId AND r.status IN ('PENDING','READY')")
    int findMaxQueuePosition(@Param("bookId") Long bookId);
}
