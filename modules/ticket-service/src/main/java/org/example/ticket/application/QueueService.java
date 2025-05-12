package org.example.ticket.application;

import java.util.UUID;

import org.example.ticket.application.dto.QueueStatusDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QueueService {

    private final RedisTemplate<String, String> redisTemplate;

    // 대기열 진입
    public void enterQueue(UUID queueToken, Integer ticketOpenId) {
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
