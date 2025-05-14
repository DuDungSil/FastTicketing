package org.example.ticket.adapter.in;

import java.util.List;

import org.example.ticket.adapter.in.request.TicketOpenRequest;
import org.example.ticket.application.TicketOpenService;
import org.example.ticket.application.dto.TicketOpenDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ticket-opens")
public class TicketOpenContoller {

    private final TicketOpenService ticketOpenService;

    // 티켓 예매 정보 post ( admin )
    @PostMapping
    public ResponseEntity<String> createTicketOpen(@RequestBody TicketOpenRequest request) {
        ticketOpenService.createTicketOpen(request.scheduleId(), request.openAt(), request.limitPerUser(),
                request.openType());
        return ResponseEntity.ok("new TicketOpen created.");
    }

    // 티켓 예매 정보 조회
    @GetMapping
    public ResponseEntity<List<TicketOpenDto>> getTicketOpens() {
        List<TicketOpenDto> TicketOpenDtoList = ticketOpenService.getAvailableTicketOpens();
        return ResponseEntity.ok(TicketOpenDtoList);
    }

    @DeleteMapping("/{ticketOpenId}")
    public ResponseEntity<Void> deleteTicketOpen(@PathVariable Integer ticketOpenId) {
        ticketOpenService.deleteTicketOpen(ticketOpenId);
        return ResponseEntity.noContent().build();
    }

}
