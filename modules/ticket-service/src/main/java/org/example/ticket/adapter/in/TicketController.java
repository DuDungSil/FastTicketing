package org.example.ticket.adapter.in;

import org.example.ticket.adapter.in.request.TicketRequest;
import org.example.ticket.application.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    // 티켓 예매
    @PostMapping("/reserve")
    public ResponseEntity<String> reserveTickets(@RequestBody TicketRequest request) {
        ticketService.holdingTickets(request.userId(), request.ticketOpenId(), request.ticketIds());
        return ResponseEntity.ok("티켓 예매 성공");
    }

    // 티켓 결제
    @PostMapping("/pay")
    public ResponseEntity<String> payTickets(@RequestBody TicketRequest request) {
        ticketService.reserveTickets(request.userId(), request.ticketOpenId(), request.ticketIds());
        return ResponseEntity.ok("티켓 결제 성공");
    }

    // 펜딩 상태 취소
    @PostMapping("/cancel/pending")
    public ResponseEntity<String> cancelPendingTickets(@RequestBody TicketRequest request) {
        ticketService.cancelPendingTickets(request.userId(), request.ticketOpenId(), request.ticketIds());
        return ResponseEntity.ok("티켓 취소 성공");
    }

    // 결제 상태 취소
    @PostMapping("/cancel/reserved")
    public ResponseEntity<String> cancelReservedTickets(@RequestBody TicketRequest request) {
        ticketService.cancelReservedTickets(request.userId(), request.ticketOpenId(), request.ticketIds());
        return ResponseEntity.ok("티켓 취소 성공");
    }
}
