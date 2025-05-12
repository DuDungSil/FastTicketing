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
public class HeldTicketExpireScheduler {

    private final TicketRepository ticketRepository;
    private final RedisTemplate<String, String> redisTemplate;

    // 매 1분마다 실행
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void cancelOverdueHeldTickets() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24); // 입금 기한
        List<Ticket> expiredHeldTickets = ticketRepository.findOverdueHeldTickets(cutoff);

        for (Ticket ticket : expiredHeldTickets) {
            ticket.cancelHeld(); // 상태: HELD → CANCELED
            ticket.makeAvailableAgain(); // 상태: CANCELED → AVAILABLE
            ticketRepository.save(ticket);

            String redisKey = "seat_status:" + ticket.getTicketOpen().getId();
            redisTemplate.opsForHash().put(redisKey, ticket.getSeatCode(), TicketStatus.AVAILABLE.name());
        }
    }

}
