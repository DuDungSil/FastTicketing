package org.example.ticket.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.example.TicketApplication;
import org.example.ticket.adapter.out.TicketOpenRepository;
import org.example.ticket.adapter.out.TicketRepository;
import org.example.ticket.domain.entity.Ticket;
import org.example.ticket.domain.entity.TicketOpen;
import org.example.ticket.domain.enums.OpenType;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TicketApplication.class)
@ActiveProfiles("test")
class ReservationServiceConcurrencyTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketOpenRepository ticketOpenRepository;

    private Long seatId;
    private Integer ticketOpenId;

    @BeforeEach
    void setUp() {
        Ticket ticket = new Ticket(1L); // 좌석 ID 1
        TicketOpen ticketOpen = TicketOpen.create(
                999, // scheduleId
                LocalDateTime.now().minusMinutes(1),
                1, // limitPerUser
                OpenType.NORMAL,
                List.of(ticket)
        );

        ticketOpenRepository.saveAndFlush(ticketOpen);

        this.seatId = ticket.getSeatId();
        this.ticketOpenId = ticketOpen.getId();
    }

    @Test
    void 동시에_2명이_같은_티켓_예매하면_1명만_성공해야_함() throws InterruptedException {
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();

        // 티켓 객체 조회
        Ticket ticket = ticketRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("테스트용 티켓이 존재하지 않습니다."));
        Long ticketId = ticket.getId();

        for (int i = 0; i < threadCount; i++) {
            final Long userId = (long) (i + 1);
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    reservationService.reserveTickets(userId, List.of(ticketId));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("예매 실패: 유저=" + userId + " → " + e.getMessage());
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();      // 준비 완료 대기
        start.countDown();  // 동시에 시작
        done.await();       // 모든 작업 완료 대기

        assertEquals(1, successCount.get(), "오직 1명만 예매 성공해야 합니다");
    }
}
