package org.example.ticket.domain.entity;

import org.example.ticket.domain.enums.TicketStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 예매
 */
@NoArgsConstructor
@Getter
@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long seatId; // 좌석 id

    @Enumerated(EnumType.STRING)
    private TicketStatus status; // AVAILABLE, PENDING, RESERVED 등

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_open_id")
    private TicketOpen ticketOpen;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    // ===== 생성자 =====
    public Ticket(Long seatId) {
        this.seatId = seatId;
        this.status = TicketStatus.AVAILABLE;
    }

    public void setTicketOpen(TicketOpen ticketOpen) {
        this.ticketOpen = ticketOpen;
    }

    // ===== 세터 =====
    void assignReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    // ===== 상태 전이 로직 =====
    public boolean isAvailable() {
        return this.status == TicketStatus.AVAILABLE;
    }

    /**
     * 예약 시도 - 원자적 연산
     *
     * @return 예약 성공 여부
     */
    public boolean tryReserve() {
        if (!isAvailable()) {
            return false;
        }
        this.status = TicketStatus.RESERVED;
        return true;
    }

    /**
     * 예약 후 취소
     */
    public void markAsCancelPending() {
        if (this.status == TicketStatus.RESERVED) {
            this.status = TicketStatus.CANCEL_PENDING;
        }
    }

    /**
     * 취소 -> 사용 가능
     */
    public void release() {
        if (this.status == TicketStatus.CANCEL_PENDING) {
            this.status = TicketStatus.AVAILABLE;
        }
    }

    /**
     * 관리자 차단 - 좌석 사용 불가 처리
     */
    public void block() {
        this.status = TicketStatus.BLOCKED;
    }

    /**
     * 차단 해제
     */
    public void unblock() {
        this.status = TicketStatus.AVAILABLE;
    }
}
