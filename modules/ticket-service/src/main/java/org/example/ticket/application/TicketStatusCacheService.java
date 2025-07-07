package org.example.ticket.application;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.example.ticket.application.dto.TicketStatusDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TicketStatusCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public TicketStatusCacheService(@Qualifier("stringRedisTemplate") RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    private String getCacheKey(Integer ticketOpenId) {
        return "ticket:status:" + ticketOpenId;
    }

    public List<TicketStatusDto> getCachedTicketStatuses(Integer ticketOpenId) {
        String key = getCacheKey(ticketOpenId);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

        return entries.entrySet().stream()
                .map(entry -> {
                    try {
                        String value = entry.getValue().toString();
                        return objectMapper.readValue(value, TicketStatusDto.class);
                    } catch (JsonProcessingException e) {
                        log.error("캐시 역직렬화 실패 - key: {}", entry.getKey(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void putTicketStatus(Integer ticketOpenId, TicketStatusDto dto) {
        String key = getCacheKey(ticketOpenId);
        try {
            String json = objectMapper.writeValueAsString(dto);
            redisTemplate.opsForHash().put(key, dto.seatId().toString(), json);
        } catch (JsonProcessingException e) {
            log.error("Redis 캐시 저장 실패 - {}", dto, e);
        }
    }

    public void putAllTicketStatuses(Integer ticketOpenId, List<TicketStatusDto> dtos) {
        String key = getCacheKey(ticketOpenId);
        Map<String, String> data = new HashMap<>();

        for (TicketStatusDto dto : dtos) {
            try {
                data.put(dto.seatId().toString(), objectMapper.writeValueAsString(dto));
            } catch (JsonProcessingException e) {
                log.error("JSON 직렬화 실패 - {}", dto, e);
            }
        }

        redisTemplate.opsForHash().putAll(key, data);
        redisTemplate.expire(key, Duration.ofHours(1));
    }

    public void clearCache(Integer ticketOpenId) {
        String key = getCacheKey(ticketOpenId);
        redisTemplate.delete(key);
        log.info("티켓 캐시 삭제 완료 - ticketOpenId: {}", ticketOpenId);
    }
}
