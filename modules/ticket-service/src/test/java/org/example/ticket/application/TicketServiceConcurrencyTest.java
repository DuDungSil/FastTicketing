package org.example.ticket.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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

@SpringBootTest
@ActiveProfiles("test")
class TicketServiceConcurrencyTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketOpenRepository ticketOpenRepository;

    private Long ticketId;
    private Integer ticketOpenId;

    @BeforeEach
    void setUp() {
        // 예매할 티켓 1개 생성
        Ticket ticket1 = new Ticket("A1");
        Ticket ticket2 = new Ticket("A2");

        // 티켓 오픈 정책 포함한 객체 생성 및 연관관계 설정
        TicketOpen ticketOpen = new TicketOpen(
                999, // scheduleId
                LocalDateTime.now().minusMinutes(1), // openAt
                1, // limitPerUser
                OpenType.NORMAL,
                List.of(ticket1, ticket2)
        );

        // 저장 (cascade로 ticket도 저장됨)
        ticketOpenRepository.saveAndFlush(ticketOpen);

        // ID 추출
        this.ticketId = ticket1.getId();
        this.ticketOpenId = ticketOpen.getId();

        ticketService.getTicketStatus(ticketOpenId);
    }

    @Test
    void 동시에_2명이_같은_티켓_예매하면_1명만_성공해야_함() throws InterruptedException {
        int threadCount = 4;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            final Long userId = Long.valueOf(i + 1);
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await(); // 모든 스레드 동시에 시작
                    ticketService.holdingTickets(userId, ticketOpenId, List.of(ticketId));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("실패한 사용자: " + userId + " → " + e.getMessage());
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await(); // 모든 스레드 준비될 때까지 기다림
        start.countDown(); // 동시에 시작
        done.await(); // 모두 끝날 때까지 대기

        assertEquals(1, successCount.get(), "오직 한 명만 예매에 성공해야 함");
    }

    // 여러명이 여러자리를 홀딩했을 때 
}
