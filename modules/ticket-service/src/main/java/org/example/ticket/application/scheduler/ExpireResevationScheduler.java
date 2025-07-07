package org.example.ticket.application.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.example.ticket.adapter.out.ReservationRepository;
import org.example.ticket.application.TicketService;
import org.example.ticket.domain.entity.Reservation;
import org.example.ticket.domain.enums.ReservationStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 예매 만료 처리 스케쥴러 - 좌석 타임아웃 - Redis TTL 이벤트 보완
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExpireResevationScheduler {

    private final TicketService ticketService;
    private final ReservationRepository reservationRepository;

    @Scheduled(fixedDelay = 60_000) // 1분마다 실행
    @Transactional
    public void expireReservations() {
        List<Reservation> toExpire = reservationRepository
                .findAllByStatusAndExpiresAtBefore(ReservationStatus.REQUESTED, LocalDateTime.now());

        for (Reservation reservation : toExpire) {
            reservation.expire(); // 상태 변경 + ticket.release()
            ticketService.updateTicketCache(reservation.getTickets().get(0).getTicketOpen().getId(), reservation.getTickets());
        }

        log.info("만료된 예약 {}건 처리 완료", toExpire.size());
    }
}
