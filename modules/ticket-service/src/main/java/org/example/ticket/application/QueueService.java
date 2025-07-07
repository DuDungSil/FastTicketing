package org.example.ticket.application;

import java.time.LocalDateTime;
import java.util.UUID;

import org.example.ticket.adapter.out.TicketOpenRepository;
import org.example.ticket.application.dto.QueueStatusDto;
import org.example.ticket.domain.entity.TicketOpen;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QueueService {

    private final RedisTemplate<String, String> redisTemplate;
    private final TicketOpenRepository ticketOpenRepository;

    // 대기열 진입
    public void enterQueue(UUID queueToken, Integer ticketOpenId) {
        TicketOpen ticketOpen = ticketOpenRepository.findById(ticketOpenId).orElseThrow();

        // 예매 시작 시간이 현재보다 이후면 예외 발생
        if (ticketOpen.getOpenAt().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("아직 예매를 시작할 수 없습니다.");
        }

        String queueKey = getQueueKey(ticketOpenId);
        double score = System.currentTimeMillis();

        redisTemplate.opsForZSet().add(queueKey, queueToken.toString(), score);
    }

    // 대기열 현황
    public QueueStatusDto getQueueStatus(UUID queueToken, Integer ticketOpenId) {
        String queueKey = getQueueKey(ticketOpenId);

        Long rank = redisTemplate.opsForZSet().rank(queueKey, queueToken.toString());
        if (rank == null) {
            throw new IllegalStateException("대기열에 없습니다.");
        }

        boolean canEnter = rank < 100;

        return new QueueStatusDto(rank + 1, canEnter);
    }

    // 대기열 퇴장
    public void leaveQueue(UUID queueToken, Integer ticketOpenId) {
        String queueKey = getQueueKey(ticketOpenId);

        redisTemplate.opsForZSet().remove(queueKey, queueToken.toString());
    }

    private String getQueueKey(Integer ticketOpenId) {
        return "queue:ticketOpen:" + ticketOpenId;
    }

}
