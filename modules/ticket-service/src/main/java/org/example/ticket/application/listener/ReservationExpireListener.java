package org.example.ticket.application.listener;

import java.nio.charset.StandardCharsets;

import org.example.ticket.adapter.out.ReservationRepository;
import org.example.ticket.application.TicketService;
import org.example.ticket.domain.entity.Reservation;
import org.example.ticket.domain.enums.ReservationStatus;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * 티켓팅 시스템의 TTL 만료 리스너
 * - Redis TTL 이벤트를 수신하여 Reservation의 REQUESTED 상태 타임아웃
 * - reservation:expire:{id} 형식의 키를 기준으로 판단
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationExpireListener {

    private final ReservationRepository reservationRepository;
    private final TicketService ticketService;

    @EventListener
    public void onKeyExpired(RedisKeyExpiredEvent<String> event) {
        String expiredKey = new String(event.getSource(), StandardCharsets.UTF_8); // reservation:expire:123

        if (expiredKey.startsWith("reservation:expire:")) {
            Long reservationId = Long.valueOf(expiredKey.replace("reservation:expire:", ""));
            log.info("예약 TTL 만료 감지 - reservationId: {}", reservationId);
            handleReservationExpire(reservationId);
        }
    }

    @Transactional
    public void handleReservationExpire(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation == null) {
            log.warn("만료된 예약 조회 실패 - ID: {}", reservationId);
            return;
        }

        if (reservation.getStatus() != ReservationStatus.REQUESTED) {
            log.info("이미 만료 처리된 예약 - ID: {}", reservationId);
            return;
        }

        reservation.expire();

        // 캐시 반영
        Integer ticketOpenId = reservation.getTickets().get(0).getTicketOpen().getId();
        ticketService.updateTicketCache(ticketOpenId, reservation.getTickets());

        log.info("예약 만료 처리 완료 - ID: {}", reservationId);
    }

}
