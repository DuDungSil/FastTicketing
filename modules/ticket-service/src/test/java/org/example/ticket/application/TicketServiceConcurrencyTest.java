package org.example.ticket.application;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.example.ticket.adapter.out.TicketRepository;
import org.example.ticket.domain.entity.Ticket;
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

    @BeforeEach
    void setUp() {
        // 예매 가능한 상태로 티켓 초기화
        Ticket ticket = ticketRepository.findById(1L).orElseThrow();
        ticket.initTicket();
        ticketRepository.save(ticket);
    }

    @Test
    void 동시에_2명이_같은_티켓_예매하면_1명만_성공해야_함() throws InterruptedException {
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        Long ticketId = 1L;
        AtomicInteger successCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            final Long userId = Long.valueOf(i + 1);
            executor.submit(() -> {
                ready.countDown(); // 준비 완료
                try {
                    start.await(); // 동시에 시작
                    ticketService.reserveTickets(userId, List.of(ticketId));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("실패한 사용자: " + userId);
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

}
