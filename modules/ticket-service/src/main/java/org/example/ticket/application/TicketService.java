package org.example.ticket.application;

import java.util.List;
import java.util.stream.Collectors;

import org.example.ticket.adapter.out.TicketRepository;
import org.example.ticket.application.dto.TicketStatusDto;
import org.example.ticket.domain.entity.Ticket;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketStatusCacheService ticketCacheService;

    /**
     * 티켓 생성 (좌석 ID 이용)
     */
    public List<Ticket> generateTickets(List<Long> seatIds) {
        return seatIds.stream()
                .map(seatId -> new Ticket(seatId))
                .collect(Collectors.toList());
    }

    /**
     * 티켓 전체 현황 조회 (Redis 캐시 우선)
     */
    @Transactional(readOnly = true)
    public List<TicketStatusDto> getTicketStatus(Integer ticketOpenId) {
        try {
            List<TicketStatusDto> cached = ticketCacheService.getCachedTicketStatuses(ticketOpenId);
            if (!cached.isEmpty()) {
                return cached;
            }

            return loadAndCacheTicketStatus(ticketOpenId);
        } catch (Exception e) {
            log.error("Redis 캐시 조회 실패, DB로 폴백", e);
            return loadTicketStatusFromDB(ticketOpenId);
        }
    }

    /**
     * 관리자용 - 티켓 차단
     */
    @Transactional
    public void blockTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 티켓입니다."));

        ticket.block();
        ticketRepository.save(ticket);

        updateTicketCache(ticket);
        log.info("티켓 차단 완료 - 티켓ID: {}", ticketId);
    }

    /**
     * 관리자용 - 티켓 차단 해제
     */
    @Transactional
    public void unblockTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 티켓입니다."));

        ticket.unblock();
        ticketRepository.save(ticket);

        updateTicketCache(ticket);
        log.info("티켓 차단 해제 완료 - 티켓ID: {}", ticketId);
    }

    /**
     * 특정 티켓 조회
     */
    @Transactional(readOnly = true)
    public Ticket getTicket(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 티켓입니다."));
    }

    // === 유틸 ===
    /**
     * 티켓 상태 캐시 업데이트
     */
    public void updateTicketCache(Ticket ticket) {
        TicketStatusDto dto = new TicketStatusDto(ticket.getId(), ticket.getSeatId(), ticket.getStatus());
        ticketCacheService.putTicketStatus(ticket.getTicketOpen().getId(), dto);
    }

    /**
     * 다수 티켓 상태 캐시 업데이트
     */
    public void updateTicketCache(Integer ticketOpenId, List<Ticket> tickets) {
        List<TicketStatusDto> dtos = tickets.stream()
                .map(ticket -> new TicketStatusDto(ticket.getId(), ticket.getSeatId(), ticket.getStatus()))
                .collect(Collectors.toList());

        ticketCacheService.putAllTicketStatuses(ticketOpenId, dtos);
    }

    /**
     * 캐시 삭제
     */
    public void clearTicketCache(Integer ticketOpenId) {
        ticketCacheService.clearCache(ticketOpenId);
    }

    /**
     * DB에서 티켓 상태 로드 후 캐시 저장
     */
    private List<TicketStatusDto> loadAndCacheTicketStatus(Integer ticketOpenId) {
        List<Ticket> tickets = ticketRepository.findAllByTicketOpenId(ticketOpenId);
        List<TicketStatusDto> dtos = tickets.stream()
                .map(t -> new TicketStatusDto(t.getId(), t.getSeatId(), t.getStatus()))
                .collect(Collectors.toList());

        ticketCacheService.putAllTicketStatuses(ticketOpenId, dtos);
        return dtos;
    }

    /**
     * DB에서 티켓 상태 직접 로드
     */
    private List<TicketStatusDto> loadTicketStatusFromDB(Integer ticketOpenId) {
        return ticketRepository.findAllByTicketOpenId(ticketOpenId).stream()
                .map(t -> new TicketStatusDto(t.getId(), t.getSeatId(), t.getStatus()))
                .collect(Collectors.toList());
    }

}
