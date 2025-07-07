package org.example.ticket.application;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.example.TicketApplication;
import org.example.ticket.application.dto.TicketStatusDto;
import org.example.ticket.domain.enums.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = TicketApplication.class)
@ActiveProfiles("test")
class TicketStatusCacheTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @InjectMocks
    private TicketStatusCacheService ticketStatusCacheService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        ticketStatusCacheService = new TicketStatusCacheService(redisTemplate, objectMapper);

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    void 단일_티켓_상태_캐시에_저장된다() throws JsonProcessingException {
        Integer ticketOpenId = 1;
        TicketStatusDto dto = new TicketStatusDto(1001L, 101L, TicketStatus.AVAILABLE);

        ticketStatusCacheService.putTicketStatus(ticketOpenId, dto);

        String key = "ticket:status:" + ticketOpenId;
        String json = objectMapper.writeValueAsString(dto);

        verify(hashOperations, times(1)).put(eq(key), eq("101"), eq(json));
    }

    @Test
    void 여러_티켓_상태_한번에_캐시에_저장된다() throws JsonProcessingException {
        Integer ticketOpenId = 2;
        List<TicketStatusDto> dtos = List.of(
                new TicketStatusDto(2001L, 201L, TicketStatus.AVAILABLE),
                new TicketStatusDto(2002L, 202L, TicketStatus.RESERVED)
        );

        Map<String, String> expectedMap = new HashMap<>();
        for (TicketStatusDto dto : dtos) {
            expectedMap.put(dto.seatId().toString(), objectMapper.writeValueAsString(dto));
        }

        ticketStatusCacheService.putAllTicketStatuses(ticketOpenId, dtos);

        String key = "ticket:status:" + ticketOpenId;
        verify(hashOperations).putAll(eq(key), eq(expectedMap));
        verify(redisTemplate).expire(eq(key), eq(Duration.ofHours(1)));
    }

    @Test
    void 캐시에_저장된_티켓_상태들을_정상적으로_조회한다() throws JsonProcessingException {
        Integer ticketOpenId = 3;
        String key = "ticket:status:" + ticketOpenId;

        Map<Object, Object> entries = Map.of(
                "301", objectMapper.writeValueAsString(new TicketStatusDto(3001L, 301L, TicketStatus.AVAILABLE)),
                "302", objectMapper.writeValueAsString(new TicketStatusDto(3002L, 302L, TicketStatus.RESERVED))
        );

        when(hashOperations.entries(key)).thenReturn(entries);

        List<TicketStatusDto> result = ticketStatusCacheService.getCachedTicketStatuses(ticketOpenId);

        assertThat(result).hasSize(2);
        assertThat(result).extracting("ticketId").containsExactlyInAnyOrder(3001L, 3002L);
        assertThat(result).extracting("status").containsExactlyInAnyOrder(TicketStatus.AVAILABLE, TicketStatus.RESERVED);
    }

    @Test
    void 특정_티켓오픈ID의_캐시를_삭제한다() {
        Integer ticketOpenId = 4;
        String key = "ticket:status:" + ticketOpenId;

        ticketStatusCacheService.clearCache(ticketOpenId);

        verify(redisTemplate).delete(key);
    }

}
