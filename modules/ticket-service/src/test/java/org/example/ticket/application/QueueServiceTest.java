package org.example.ticket.application;

import org.example.TicketApplication;
import org.example.ticket.adapter.out.TicketOpenRepository;
import org.example.ticket.application.dto.QueueStatusDto;
import org.example.ticket.domain.entity.TicketOpen;
import org.example.ticket.domain.enums.OpenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = TicketApplication.class)
@ActiveProfiles("test")
public class QueueServiceTest {

    @Autowired
    private QueueService queueService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private TicketOpenRepository ticketOpenRepository;

    private final Integer ticketOpenId = 1;

    @BeforeEach
    void setUp() {
        // Redis queue 초기화
        redisTemplate.delete("queue:ticketOpen:" + ticketOpenId);

        // ticketOpen 엔티티 저장 (예매 시작시간은 과거로)
        TicketOpen ticketOpen = TicketOpen.create(
                ticketOpenId,
                LocalDateTime.now().minusMinutes(5),
                1,
                OpenType.NORMAL,
                List.of()
        );
        ticketOpenRepository.save(ticketOpen);
    }

    @Test
    void UUID_2개가_순차적으로_대기열에_진입하면_순위가_올바르게_부여된다() {
        UUID token1 = UUID.randomUUID();
        UUID token2 = UUID.randomUUID();

        queueService.enterQueue(token1, ticketOpenId);
        queueService.enterQueue(token2, ticketOpenId);

        QueueStatusDto status1 = queueService.getQueueStatus(token1, ticketOpenId);
        QueueStatusDto status2 = queueService.getQueueStatus(token2, ticketOpenId);

        assertThat(status1.position()).isEqualTo(1);
        assertThat(status1.canEnter()).isTrue();

        assertThat(status2.position()).isEqualTo(2);
        assertThat(status2.canEnter()).isTrue();
    }

    @Test
    void UUID가_대기열에_진입후_퇴장하면_조회시_예외발생() {
        UUID token = UUID.randomUUID();
        queueService.enterQueue(token, ticketOpenId);
        queueService.leaveQueue(token, ticketOpenId);

        assertThrows(IllegalStateException.class, () -> {
            queueService.getQueueStatus(token, ticketOpenId);
        });
    }

    @Test
    void 동시_1000명_UUID_진입시_순위가_모두_부여된다() throws InterruptedException {
        int userCount = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(userCount);
        UUID[] tokens = new UUID[userCount];

        for (int i = 0; i < userCount; i++) {
            tokens[i] = UUID.randomUUID();
        }

        for (int i = 0; i < userCount; i++) {
            final int index = i;
            executor.execute(() -> {
                try {
                    queueService.enterQueue(tokens[index], ticketOpenId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        for (UUID token : tokens) {
            Long rank = redisTemplate.opsForZSet().rank("queue:ticketOpen:" + ticketOpenId, token.toString());
            assertThat(rank).isNotNull();
        }
    }

    @Test
    void 여러_UUID가_순차적으로_진입하고_중간에_퇴장하면_순위가_재조정된다() {
        UUID token1 = UUID.randomUUID();
        UUID token2 = UUID.randomUUID();
        UUID token3 = UUID.randomUUID();
        UUID token4 = UUID.randomUUID();
        UUID token5 = UUID.randomUUID();

        queueService.enterQueue(token1, ticketOpenId);
        sleep(1);
        queueService.enterQueue(token2, ticketOpenId);
        sleep(1);
        queueService.enterQueue(token3, ticketOpenId);
        sleep(1);
        queueService.enterQueue(token4, ticketOpenId);
        sleep(1);
        queueService.enterQueue(token5, ticketOpenId);

        queueService.leaveQueue(token2, ticketOpenId);
        queueService.leaveQueue(token4, ticketOpenId);

        QueueStatusDto status1 = queueService.getQueueStatus(token1, ticketOpenId);
        QueueStatusDto status3 = queueService.getQueueStatus(token3, ticketOpenId);
        QueueStatusDto status5 = queueService.getQueueStatus(token5, ticketOpenId);

        assertThat(status1.position()).isEqualTo(1); // unchanged
        assertThat(status3.position()).isEqualTo(2); // token2 gone
        assertThat(status5.position()).isEqualTo(3); // token4 gone
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
