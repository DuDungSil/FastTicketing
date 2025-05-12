package org.example.ticket.application.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.example.ticket.adapter.out.TicketRepository;
import org.example.ticket.domain.entity.Ticket;
import org.example.ticket.domain.enums.TicketStatus;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/*
 * 타임아웃 PENDING 상태 좌석 만료 처리 스케쥴러
 */
@Component
@RequiredArgsConstructor
public class PendingTicketCleaner {

    private final TicketRepository ticketRepository;
    private final RedisTemplate<String, String> redisTemplate;

    // 1분마다 실행
    // db의 전체 티켓을 다 도는 문제? 
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void cleanExpiredPendingTickets() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(7);
        List<Ticket> pendingTickets = ticketRepository.findPendingOlderThan(cutoffTime);

        for (Ticket ticket : pendingTickets) {
            String ttlKey = "ticket:pending:" + ticket.getId();

            if (!Boolean.TRUE.equals(redisTemplate.hasKey(ttlKey))) {
                ticket.release(); // AVAILABLE로 복구
                ticketRepository.save(ticket);

                String redisKey = "seat_status:" + ticket.getTicketOpen().getId();
                redisTemplate.opsForHash().put(redisKey, ticket.getSeatCode(), TicketStatus.AVAILABLE.name());
            }
        }
    }

}
