package org.example.ticket.adapter.in;

import java.util.List;

import org.example.ticket.adapter.in.request.TicketRequest;
import org.example.ticket.application.ReservationService;
import org.example.ticket.application.dto.ReservationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reserve")
    public ResponseEntity<ReservationDto> reserveTickets(@RequestBody TicketRequest request) {
        ReservationDto reservationDto = reservationService.reserveTickets(request.userId(), request.ticketIds());
        return ResponseEntity.ok(reservationDto);
    }

    @PostMapping("/payment/start")
    public ResponseEntity<String> startPayment(@RequestParam Long reservationId,
            @RequestParam String paymentId) {
        reservationService.startPayment(reservationId, paymentId);
        return ResponseEntity.ok("결제 시작됨");
    }

    @PostMapping("/payment/confirm")
    public ResponseEntity<String> confirmPayment(@RequestParam Long reservationId) {
        reservationService.confirmPayment(reservationId);
        return ResponseEntity.ok("결제 완료됨");
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelReservation(@RequestParam Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.ok("예약 취소됨");
    }
}
