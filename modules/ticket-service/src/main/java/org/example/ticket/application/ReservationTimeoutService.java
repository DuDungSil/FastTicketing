package org.example.ticket.application;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationTimeoutService {

    private final RedisTemplate<String, String> redisTemplate;

    public void registerReservationTTL(Long reservationId, LocalDateTime expiresAt) {
        String key = buildTTLKey(reservationId);

        long seconds = java.time.Duration.between(LocalDateTime.now(), expiresAt).getSeconds();
        if (seconds <= 0) {
            return; // 과거 시간 방지
        }
        redisTemplate.opsForValue().set(key, "1", Duration.ofSeconds(seconds));
    }

    public String buildTTLKey(Long reservationId) {
        return "reservation:expire:" + reservationId;
    }

}
