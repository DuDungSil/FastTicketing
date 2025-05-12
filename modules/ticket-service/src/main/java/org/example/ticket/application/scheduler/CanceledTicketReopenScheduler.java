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

@Component
@RequiredArgsConstructor
public class CanceledTicketReopenScheduler {

    private final TicketRepository ticketRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Scheduled(fixedDelay = 60000) // 1분마다 실행
    @Transactional
    public void reopenCanceledTickets() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(5); // 기준시간
        List<Ticket> canceledTickets = ticketRepository.findCanceledBefore(cutoff); // 취소표는 취소한순간 끝?? 아니라면 바꿔야함

        for (Ticket ticket : canceledTickets) {
            ticket.makeAvailableAgain(); // 상태 복구
            ticketRepository.save(ticket);

            // Redis 캐시 갱신
            String redisKey = "seat_status:" + ticket.getTicketOpen().getId();
            redisTemplate.opsForHash().put(redisKey, ticket.getSeatCode(), TicketStatus.AVAILABLE.name());
        }
    }

}
