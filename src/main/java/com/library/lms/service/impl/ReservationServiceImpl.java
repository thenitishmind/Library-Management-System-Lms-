package com.library.lms.service.impl;

import com.library.lms.dto.request.ReservationRequest;
import com.library.lms.entity.*;
import com.library.lms.entity.enums.ReservationStatus;
import com.library.lms.exception.BusinessException;
import com.library.lms.exception.ResourceNotFoundException;
import com.library.lms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_MEMBER','ROLE_LIBRARIAN','ROLE_ADMIN')")
    public Reservation placeReservation(ReservationRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
            .orElseThrow(() -> new ResourceNotFoundException("Member", request.getMemberId()));
        Book book = bookRepository.findById(request.getBookId())
            .orElseThrow(() -> new ResourceNotFoundException("Book", request.getBookId()));

        boolean alreadyReserved = reservationRepository.existsByMemberIdAndBookIdAndStatusIn(
            member.getId(), book.getId(),
            java.util.Arrays.asList(ReservationStatus.PENDING, ReservationStatus.READY)
        );
        if (alreadyReserved) {
            throw new BusinessException("Member already has an active reservation for this book");
        }

        int nextPosition = reservationRepository.findMaxQueuePosition(book.getId()) + 1;

        Reservation reservation = Reservation.builder()
            .member(member)
            .book(book)
            .reservationDate(LocalDateTime.now())
            .queuePosition(nextPosition)
            .status(ReservationStatus.PENDING)
            .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("Reservation placed id={} queue={}", savedReservation.getId(), nextPosition);
        return savedReservation;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_MEMBER','ROLE_LIBRARIAN','ROLE_ADMIN')")
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ResourceNotFoundException("Reservation", reservationId));

        if (reservation.getStatus() == ReservationStatus.FULFILLED
            || reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessException("Reservation cannot be cancelled in status: " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        log.info("Cancelled reservation id={}", reservationId);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public void fulfillReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ResourceNotFoundException("Reservation", reservationId));
        reservation.setStatus(ReservationStatus.FULFILLED);
        reservationRepository.save(reservation);
        log.info("Fulfilled reservation id={}", reservationId);
    }

    @Transactional(readOnly = true)
    public List<Reservation> getQueueForBook(Long bookId) {
        return reservationRepository.findQueueForBook(bookId);
    }
}
