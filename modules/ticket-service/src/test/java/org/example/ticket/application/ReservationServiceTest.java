package org.example.ticket.application;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.example.TicketApplication;
import org.example.ticket.adapter.out.TicketOpenRepository;
import org.example.ticket.adapter.out.TicketRepository;
import org.example.ticket.application.dto.ReservationDto;
import org.example.ticket.domain.entity.Ticket;
import org.example.ticket.domain.entity.TicketOpen;
import org.example.ticket.domain.enums.OpenType;
import org.example.ticket.domain.enums.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TicketApplication.class)
@ActiveProfiles("test")
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketOpenRepository ticketOpenRepository;

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        ticket = new Ticket(101L);  // seatId만 사용
        TicketOpen ticketOpen = TicketOpen.create(
                2000, LocalDateTime.now().minusMinutes(1), 1, OpenType.NORMAL, List.of(ticket)
        );
        ticketOpenRepository.saveAndFlush(ticketOpen);
    }

    @Test
    void 티켓_예매_성공() {
        Long userId = 1L;
        ReservationDto result = reservationService.reserveTickets(userId, List.of(ticket.getId()));

        assertThat(result).isNotNull();
        assertThat(result.tickets()).hasSize(1); // 예약된 티켓 수가 1개인지 확인
        assertThat(result.status()).isEqualTo(ReservationStatus.REQUESTED); // 상태 확인
    }

    @Test
    void 예매_제한_초과시_실패() {
        Long userId = 2L;
        Ticket ticket1 = new Ticket(201L);
        Ticket ticket2 = new Ticket(202L);
        TicketOpen open = TicketOpen.create(
                2001, LocalDateTime.now().minusMinutes(1), 1, OpenType.NORMAL, List.of(ticket1, ticket2)
        );
        ticketOpenRepository.saveAndFlush(open);

        reservationService.reserveTickets(userId, List.of(ticket1.getId()));

        assertThatThrownBy(() -> reservationService.reserveTickets(userId, List.of(ticket2.getId())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("예매 가능 수량 초과");
    }
}
