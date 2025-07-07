package org.example.ticket.application.scheduler;

import java.util.List;

import org.example.ticket.adapter.out.TicketRepository;
import org.example.ticket.application.TicketService;
import org.example.ticket.domain.entity.Ticket;
import org.example.ticket.domain.enums.TicketStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 취소표 처리 스케쥴러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReleaseCancelPendingTicketScheduler {

    private final TicketService ticketService;
    private final TicketRepository ticketRepository;

    @Scheduled(cron = "0 */5 * * * *") // 5분마다 실행
    @Transactional
    public void releaseCancelPendingTickets() {
        List<Ticket> pendingTickets = ticketRepository.findByStatus(TicketStatus.CANCEL_PENDING);

        for (Ticket ticket : pendingTickets) {
            ticket.release(); // 상태: AVAILABLE
            ticketService.updateTicketCache(ticket);
        }

        log.info("취소표 {}건을 다시 풀었습니다.", pendingTickets.size());
    }

}
