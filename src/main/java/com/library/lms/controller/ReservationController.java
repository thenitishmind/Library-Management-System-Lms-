package com.library.lms.controller;

import com.library.lms.dto.request.ReservationRequest;
import com.library.lms.entity.Reservation;
import com.library.lms.service.impl.ReservationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Reservation management")
public class ReservationController {

    private final ReservationServiceImpl reservationService;

    @PostMapping
    @Operation(summary = "Place a reservation")
    public ResponseEntity<Reservation> placeReservation(@Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.placeReservation(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a reservation")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }
}
