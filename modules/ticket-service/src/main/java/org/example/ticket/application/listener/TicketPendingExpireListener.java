package org.example.ticket.application.listener;

import java.nio.charset.StandardCharsets;

import org.example.ticket.adapter.out.TicketRepository;
import org.example.ticket.domain.entity.Ticket;
import org.example.ticket.domain.enums.TicketStatus;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/*
 * 티켓팅 시스템의 TTL 만료 리스너
 * - Redis TTL 이벤트를 수신하여 PENDING 상태 좌석을 복구
 * - ticket:pending:{id} 형식의 키를 기준으로 판단
 */
@Component
@RequiredArgsConstructor
public class TicketPendingExpireListener {

    private final TicketRepository ticketRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @EventListener
    public void onKeyExpired(RedisKeyExpiredEvent<String> event) {
        String key = new String(event.getSource(), StandardCharsets.UTF_8); // ticket:pending:123
        if (key.startsWith("ticket:pending:")) {
            Long ticketId = Long.valueOf(key.replace("ticket:pending:", ""));
            restoreTicket(ticketId);
        }
    }

    @Transactional
    public void restoreTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket != null && ticket.getStatus() == TicketStatus.PENDING) {
            ticket.release(); // AVAILABLE
            ticketRepository.save(ticket);

            String redisKey = getSeatCacheKey(ticket.getTicketOpen().getId());
            redisTemplate.opsForHash().put(redisKey, ticket.getSeatCode(), TicketStatus.AVAILABLE.name());
        }
    }

    private String getSeatCacheKey(Integer ticketOpenId) {
        return "seat_status:" + ticketOpenId;
    }

}
