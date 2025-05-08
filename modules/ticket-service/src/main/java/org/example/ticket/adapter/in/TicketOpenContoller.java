package org.example.ticket.adapter.in;

import org.example.ticket.adapter.in.request.TicketOpenRequest;
import org.example.ticket.application.TicketOpenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ticket-opens")
public class TicketOpenContoller {

    private final TicketOpenService ticketOpenService;

    // 티켓 예매 정보 post ( admin )
    @PostMapping
    public ResponseEntity<String> createTicketOpen(@RequestBody TicketOpenRequest request) {
        ticketOpenService.createTicketOpen(request.scheduleId(), request.openAt(), request.limitPerUser(), request.openType());
        return ResponseEntity.ok("new TicketOpen created.");
    }

}
