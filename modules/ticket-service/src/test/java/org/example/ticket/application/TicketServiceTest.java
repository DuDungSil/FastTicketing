package org.example.ticket.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.example.TicketApplication;
import org.example.performance.domain.entity.Seat;
import org.example.ticket.adapter.out.TicketOpenRepository;
import org.example.ticket.adapter.out.TicketRepository;
import org.example.ticket.domain.entity.Ticket;
import org.example.ticket.domain.entity.TicketOpen;
import org.example.ticket.domain.enums.OpenType;
import org.example.ticket.domain.enums.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest(classes = TicketApplication.class)
class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketOpenRepository ticketOpenRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    private TicketOpen defaultTicketOpen;
    private Ticket defaultTicket;

    @BeforeEach
    void 공통셋업() {
        Ticket ticket = new Ticket("A1");
        TicketOpen open = new TicketOpen(
                999, LocalDateTime.now().minusMinutes(1), 1, OpenType.NORMAL, List.of(ticket)
        );
        defaultTicketOpen = ticketOpenRepository.saveAndFlush(open);
        defaultTicket = ticketRepository.findById(ticket.getId()).orElseThrow();
        ticketService.getTicketStatus(defaultTicketOpen.getId());
    }

    @Test
    void 티켓_생성_테스트() {
        List<Seat> seats = List.of(new Seat("A1"), new Seat("A2"));

        List<Ticket> tickets = ticketService.generateTickets(seats);

        assertThat(tickets).hasSize(2);
        assertThat(tickets.get(0).getSeatCode()).isEqualTo("A1");
    }

    @Test
    void 티켓_선점_성공_테스트() {
        Long userId = 1L;

        ticketService.holdingTickets(userId, defaultTicketOpen.getId(), List.of(defaultTicket.getId()));

        Ticket result = ticketRepository.findById(defaultTicket.getId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(TicketStatus.PENDING);
        assertThat(result.getUserId()).isEqualTo(userId);
    }

    @Test
    void 티켓_예매_테스트() {
        Long userId = 1L;
        ticketService.holdingTickets(userId, defaultTicketOpen.getId(), List.of(defaultTicket.getId()));

        ticketService.reserveTickets(userId, defaultTicketOpen.getId(), List.of(defaultTicket.getId()));

        Ticket reserved = ticketRepository.findById(defaultTicket.getId()).orElseThrow();
        assertThat(reserved.getStatus()).isEqualTo(TicketStatus.RESERVED);
    }

    @Test
    void 티켓_선점_취소_테스트() {
        Long userId = 1L;
        ticketService.holdingTickets(userId, defaultTicketOpen.getId(), List.of(defaultTicket.getId()));

        ticketService.cancelPendingTickets(userId, defaultTicketOpen.getId(), List.of(defaultTicket.getId()));

        Ticket canceled = ticketRepository.findById(defaultTicket.getId()).orElseThrow();
        assertThat(canceled.getStatus()).isEqualTo(TicketStatus.AVAILABLE);
    }

    @Test
    void 티켓_결제후_취소_테스트() {
        Long userId = 1L;
        ticketService.holdingTickets(userId, defaultTicketOpen.getId(), List.of(defaultTicket.getId()));
        ticketService.reserveTickets(userId, defaultTicketOpen.getId(), List.of(defaultTicket.getId()));

        ticketService.cancelReservedTickets(userId, defaultTicketOpen.getId(), List.of(defaultTicket.getId()));

        Ticket refunded = ticketRepository.findById(defaultTicket.getId()).orElseThrow();
        assertThat(refunded.getStatus()).isEqualTo(TicketStatus.CANCELED);
    }

    @Test
    void 예매수량_초과시_예외발생_테스트() {
        Long userId = 2L;
        Ticket ticket1 = new Ticket("B1");
        Ticket ticket2 = new Ticket("B2");
        TicketOpen open = new TicketOpen(
                1001, LocalDateTime.now().minusMinutes(1), 1, OpenType.NORMAL, List.of(ticket1, ticket2)
        );
        ticketOpenRepository.saveAndFlush(open);
        Integer openId = open.getId();

        ticketService.holdingTickets(userId, openId, List.of(ticket1.getId()));

        assertThatThrownBy(()
                -> ticketService.holdingTickets(userId, openId, List.of(ticket2.getId()))
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("예매 가능 수량 초과");
    }

    @Test
    void Redis_캐시_정상작동_테스트() {
        String redisKey = "seat_status:" + defaultTicketOpen.getId();
        redisTemplate.delete(redisKey);

        assertThat(redisTemplate.opsForHash().entries(redisKey)).isEmpty();

        ticketService.getTicketStatus(defaultTicketOpen.getId());

        assertThat(redisTemplate.opsForHash().entries(redisKey)).isNotEmpty();
    }

    @Test
    void TTL_키_정상설정_테스트() {
        Long userId = 3L;
        ticketService.holdingTickets(userId, defaultTicketOpen.getId(), List.of(defaultTicket.getId()));

        String ttlKey = "ticket:pending:" + defaultTicket.getId();
        Long ttl = redisTemplate.getExpire(ttlKey, TimeUnit.MINUTES);

        assertThat(ttl).isPositive();
        assertThat(ttl).isLessThanOrEqualTo(7);
    }
}
