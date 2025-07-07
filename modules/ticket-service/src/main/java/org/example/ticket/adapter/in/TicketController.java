package org.example.ticket.adapter.in;

import java.util.List;

import org.example.ticket.application.TicketOpenService;
import org.example.ticket.application.TicketService;
import org.example.ticket.application.dto.TicketOpenDto;
import org.example.ticket.application.dto.TicketStatusDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final TicketOpenService ticketOpenService;

    @GetMapping("/status")
    public ResponseEntity<List<TicketStatusDto>> getTicketStatus(@RequestParam Integer ticketOpenId) {
        List<TicketStatusDto> statusList = ticketService.getTicketStatus(ticketOpenId);
        return ResponseEntity.ok(statusList);
    }

    @GetMapping("/ticket-open")
    public ResponseEntity<TicketOpenDto> getTicketOpen(@RequestParam Integer ticketOpenId) {
        TicketOpenDto ticketOpen = ticketOpenService.getTicketOpen(ticketOpenId);
        return ResponseEntity.ok(ticketOpen);
    }

}
