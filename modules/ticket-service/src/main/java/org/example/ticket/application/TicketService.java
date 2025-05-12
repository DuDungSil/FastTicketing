package org.example.ticket.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.example.performance.domain.entity.Seat;
import org.example.ticket.adapter.out.TicketOpenRepository;
import org.example.ticket.adapter.out.TicketRepository;
import org.example.ticket.application.dto.TicketStatusDto;
import org.example.ticket.domain.entity.Ticket;
import org.example.ticket.domain.entity.TicketOpen;
import org.example.ticket.domain.enums.TicketStatus;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TicketService {

    @PersistenceContext
    private EntityManager em;
    private final TransactionTemplate transactionTemplate;

    private final RedissonClient redissonClient;
    private final RedisTemplate<String, String> redisTemplate;

    private final TicketRepository ticketRepository;
    private final TicketOpenRepository ticketOpenRepository;

    // 티켓 생성 ( 좌석 정보 이용 )
    public List<Ticket> generateTickets(List<Seat> seats) {
        List<Ticket> newTickets = seats.stream().map(seat -> new Ticket(seat.getSeatCode()))
                .collect(Collectors.toList());

        return newTickets;
    }

    // 티켓 전체 현황
    public List<TicketStatusDto> getTicketStatus(Integer ticketOpenId) {
        // Redis 캐시 먼저 확인
        String redisKey = getSeatCacheKey(ticketOpenId);
        Map<Object, Object> cachedStatus = redisTemplate.opsForHash().entries(redisKey);

        if (!cachedStatus.isEmpty()) {
            return cachedStatus.entrySet().stream()
                    .map(e -> new TicketStatusDto(null, e.getKey().toString(), TicketStatus.valueOf(e.getValue().toString())))
                    .collect(Collectors.toList());
        }

        // Redis 없으면 DB 조회 + 캐싱
        List<Ticket> tickets = ticketRepository.findAllByTicketOpenId(ticketOpenId);
        for (Ticket ticket : tickets) {
            redisTemplate.opsForHash().put(redisKey, ticket.getSeatCode(), ticket.getStatus().name());
        }

        return tickets.stream()
                .map(ticket -> new TicketStatusDto(ticket.getId(), ticket.getSeatCode(), ticket.getStatus()))
                .collect(Collectors.toList());
    }

    // 티켓 선점
    public void holdingTickets(Long userId, Integer ticketOpenId, List<Long> ticketIds) {
        // 인당 예매 제한 검사
        TicketOpen open = ticketOpenRepository.findById(ticketOpenId).orElseThrow();
        int alreadyReserved = ticketRepository.countByUserIdAndTicketOpenId(userId, ticketOpenId);
        if (alreadyReserved + ticketIds.size() > open.getLimitPerUser()) {
            throw new IllegalStateException("예매 가능 수량 초과");
        }

        List<RLock> acquiredLocks = new ArrayList<>();

        try {
            // 1. 모든 락 선점
            for (Long ticketId : ticketIds) {
                RLock lock = redissonClient.getLock("lock:ticket:" + ticketId);
                if (lock.tryLock(3, 5, TimeUnit.SECONDS)) {
                    acquiredLocks.add(lock);
                } else {
                    throw new IllegalStateException("좌석 락 획득 실패");
                }
            }

            List<Long> successfulTicketIds = new ArrayList<>();

            // 2. 트랜잭션으로 홀딩 처리
            transactionTemplate.executeWithoutResult(status -> {
                for (Long ticketId : ticketIds) {
                    Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();
                    ticket.hold(userId);
                    ticketRepository.save(ticket);
                    successfulTicketIds.add(ticketId);

                    // 캐시 갱신
                    String redisKey = getSeatCacheKey(ticketOpenId);
                    redisTemplate.opsForHash().put(redisKey, ticket.getSeatCode(), ticket.getStatus().name());
                }
                em.flush(); // 즉시 반영
            });

            // 3. 트랜잭션 성공 후에 TTL Key 설정
            for (Long ticketId : successfulTicketIds) {
                String ttlKey = "ticket:pending:" + ticketId;
                redisTemplate.opsForValue().set(ttlKey, userId.toString(), 7, TimeUnit.MINUTES);
            }

        } catch (Exception e) {
            throw new RuntimeException("예매 처리 중 실패: " + e.getMessage(), e);
        } finally {
            // 4. 락 해제
            for (RLock lock : acquiredLocks) {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    // 티켓 예매
    @Transactional
    public void reserveTickets(Long userId, Integer ticketOpenId, List<Long> ticketIds) {
        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);
        String redisKey = getSeatCacheKey(ticketOpenId);

        // 실제 결제 로직?
        // 예매 처리
        for (Ticket ticket : tickets) {
            ticket.reserve(userId);
            ticketRepository.save(ticket);
            redisTemplate.opsForHash().put(redisKey, ticket.getSeatCode(), ticket.getStatus().name());
        }
    }

    // 결제 미 완료 시 취소 ( PENDING 상태에서 )
    @Transactional
    public void cancelPendingTickets(Long userId, Integer ticketOpenId, List<Long> ticketIds) {
        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);
        String redisKey = getSeatCacheKey(ticketOpenId);

        // 취소 처리
        for (Ticket ticket : tickets) {
            ticket.cancelPending(userId);
            ticketRepository.save(ticket);
            redisTemplate.opsForHash().put(redisKey, ticket.getSeatCode(), ticket.getStatus().name());
        }
    }

    // 결제 완료 시 취소 ( RESERVED 상태에서 )
    @Transactional
    public void cancelReservedTickets(Long userId, Integer ticketOpenId, List<Long> ticketIds) {
        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);
        String redisKey = getSeatCacheKey(ticketOpenId);

        // 실제 환불 로직?
        // 환불 처리
        for (Ticket ticket : tickets) {
            ticket.cancelReserved(userId);
            ticketRepository.save(ticket);
            redisTemplate.opsForHash().put(redisKey, ticket.getSeatCode(), ticket.getStatus().name());
        }
    }

    private String getSeatCacheKey(Integer ticketOpenId) {
        return "seat_status:" + ticketOpenId;
    }
}
