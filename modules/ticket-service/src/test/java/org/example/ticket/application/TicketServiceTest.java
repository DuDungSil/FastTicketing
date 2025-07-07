package org.example.ticket.application;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.TicketApplication;
import org.example.ticket.adapter.out.TicketOpenRepository;
import org.example.ticket.adapter.out.TicketRepository;
import org.example.ticket.domain.entity.Ticket;
import org.example.ticket.domain.entity.TicketOpen;
import org.example.ticket.domain.enums.OpenType;
import org.example.ticket.domain.enums.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TicketApplication.class)
@ActiveProfiles("test")
class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketOpenRepository ticketOpenRepository;

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        ticket = new Ticket(1L); // seatId 직접 지정
        TicketOpen ticketOpen = TicketOpen.create(
                1000, LocalDateTime.now().minusMinutes(1), 2, OpenType.NORMAL, List.of(ticket)
        );
        ticketOpenRepository.saveAndFlush(ticketOpen);
    }

    @Test
    void 티켓_생성_성공() {
        List<Long> seatIds = List.of(10L, 11L);
        List<Ticket> tickets = ticketService.generateTickets(seatIds);
        assertThat(tickets).hasSize(2);
        assertThat(tickets.get(0).getSeatId()).isEqualTo(10L);
    }

    @Test
    void 티켓_차단_및_해제() {
        ticketService.blockTicket(ticket.getId());
        Ticket blocked = ticketRepository.findById(ticket.getId()).orElseThrow();
        assertThat(blocked.getStatus()).isEqualTo(TicketStatus.BLOCKED);

        ticketService.unblockTicket(ticket.getId());
        Ticket unblocked = ticketRepository.findById(ticket.getId()).orElseThrow();
        assertThat(unblocked.getStatus()).isEqualTo(TicketStatus.AVAILABLE);
    }
}
