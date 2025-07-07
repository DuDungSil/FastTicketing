package org.example.ticket.application;

import java.time.LocalDateTime;
import java.util.List;

import org.example.ticket.adapter.out.ReservationRepository;
import org.example.ticket.adapter.out.TicketOpenRepository;
import org.example.ticket.adapter.out.TicketRepository;
import org.example.ticket.application.dto.ReservationDto;
import org.example.ticket.domain.entity.Reservation;
import org.example.ticket.domain.entity.Ticket;
import org.example.ticket.domain.entity.TicketOpen;
import org.example.ticket.domain.enums.ReservationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TicketRepository ticketRepository;
    private final TicketOpenRepository ticketOpenRepository;
    private final TicketService ticketService;
    private final ReservationTimeoutService reservationTimeoutService;

    /**
     * 유저가 좌석들을 선택해서 예매 시도
     */
    @Transactional
    public ReservationDto reserveTickets(Long userId, List<Long> ticketIds) {
        long startTime = System.currentTimeMillis();
        log.info("예약 시작 시간: {}ms, userId={}, ticketIds={}", startTime, userId, ticketIds);

        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);

        if (tickets.size() != ticketIds.size()) {
            log.warn("예약 실패 - 유효하지 않은 티켓 포함. userId: {}, ticketIds: {}", userId, ticketIds);
            throw new IllegalArgumentException("유효하지 않은 티켓이 포함되어 있습니다.");
        }

        // 모든 티켓이 동일한 ticketOpenId를 갖는지 검증
        Integer ticketOpenId = extractCommonTicketOpenIdOrThrow(tickets);

        // 예매제한 검증
        validateUserReservationLimit(userId, ticketOpenId, tickets.size());

        // Reservation 생성 시 tickets 내부에서 tryReserve 수행 및 관계 설정
        Reservation reservation = new Reservation(userId, tickets);
        Reservation saved = reservationRepository.save(reservation);

        // TTL 등록 ( 타임아웃 )
        reservationTimeoutService.registerReservationTTL(
                saved.getId(),
                saved.getExpiresAt()
        );

        // 캐시 반영
        ticketService.updateTicketCache(ticketOpenId, tickets);

        log.info("예약 성공 시간 : {}ms - reservationId: {}, userId: {}", System.currentTimeMillis() - startTime, saved.getId(), userId);
        return ReservationDto.fromEntity(saved);
    }

    /**
     * 결제 시작
     */
    @Transactional
    public void startPayment(Long reservationId, String paymentId) {
        Reservation reservation = getReservationOrThrow(reservationId);
        reservation.startPayment(paymentId);
    }

    /**
     * 결제 완료
     */
    @Transactional
    public void confirmPayment(Long reservationId) {
        Reservation reservation = getReservationOrThrow(reservationId);
        reservation.confirmPayment();
    }

    /**
     * 예약 취소
     */
    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = getReservationOrThrow(reservationId);
        reservation.cancel();

        Integer ticketOpenId = reservation.getTickets().get(0).getTicketOpen().getId();

        ticketService.updateTicketCache(ticketOpenId, reservation.getTickets());
    }

    /**
     * 만료 처리 - 배치나 스케줄러에서 호출
     */
    @Transactional
    public void expireReservations() {
        List<Reservation> toExpire = reservationRepository
                .findAllByStatusAndExpiresAtBefore(ReservationStatus.REQUESTED, LocalDateTime.now());

        for (Reservation reservation : toExpire) {
            reservation.expire();

            Integer ticketOpenId = reservation.getTickets().get(0).getTicketOpen().getId();
            ticketService.updateTicketCache(ticketOpenId, reservation.getTickets());
        }
    }

    // ===== 유틸 =====
    /**
     * 유저 예매 제한 검증
     */
    private void validateUserReservationLimit(Long userId, Integer ticketOpenId, int requestedCount) {
        TicketOpen ticketOpen = ticketOpenRepository.findById(ticketOpenId)
                .orElseThrow(() -> new IllegalArgumentException("티켓 오픈 정보를 찾을 수 없습니다."));

        int currentCount = reservationRepository.countReservedByUserIdAndTicketOpenId(userId, ticketOpenId);

        if (currentCount + requestedCount > ticketOpen.getLimitPerUser()) {
            throw new IllegalStateException("예매 수량 제한을 초과했습니다.");
        }
    }

    /**
     * 같은 회차 티켓 검증 & ticketOpen 추출
     */
    private Integer extractCommonTicketOpenIdOrThrow(List<Ticket> tickets) {
        if (tickets.isEmpty()) {
            throw new IllegalArgumentException("티켓 목록이 비어있습니다.");
        }

        Integer ticketOpenId = tickets.get(0).getTicketOpen().getId();
        boolean allMatch = tickets.stream()
                .allMatch(t -> t.getTicketOpen().getId().equals(ticketOpenId));

        if (!allMatch) {
            throw new IllegalArgumentException("모든 티켓은 동일한 티켓 오픈에 속해야 합니다.");
        }

        return ticketOpenId;
    }

    private Reservation getReservationOrThrow(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
    }
}
