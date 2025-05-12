package org.example.ticket.domain.entity;

import java.time.LocalDateTime;

import org.example.ticket.domain.enums.TicketStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "ticket_open_id")
    private TicketOpen ticketOpen;

    // Seat 위치 정보
    private String seatCode;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    private LocalDateTime pendingAt;

    private LocalDateTime heldAt;

    private LocalDateTime reservedAt;

    // ==== 비즈니스 로직 ====
    // 생성자
    public Ticket(String seatCode) {
        this.seatCode = seatCode;
        this.status = TicketStatus.AVAILABLE;
    }

    // 예매 정책 설정
    public void setTicketOpen(TicketOpen ticketOpen) {
        this.ticketOpen = ticketOpen;
    }

    // 티켓 소유 여부
    public boolean isOwnedBy(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    // 예매 가능 여부
    public boolean isAvailable() {
        return this.status == TicketStatus.AVAILABLE;
    }

    // 펜딩 상태 여부
    public boolean isPendingAvailable() {
        return this.status == TicketStatus.PENDING;
    }

    // 예매 상태 여부
    public boolean isReserved() {
        return this.status == TicketStatus.RESERVED;
    }

    // 입금 대기 상태 여부
    public boolean isHeld() {
        return this.status == TicketStatus.HELD;
    }

    // 취소 상태 여부
    public boolean isCancelled() {
        return this.status == TicketStatus.CANCELED;
    }

    // 선점
    public void hold(Long userId) {
        if (!isAvailable()) {
            throw new IllegalStateException("예매할 수 없는 티켓입니다.");
        }
        this.status = TicketStatus.PENDING;
        this.userId = userId;
        this.pendingAt = LocalDateTime.now();
    }

    // 예매 ( 결제 o )
    public void reserve(Long userId) {
        if (!isOwnedBy(userId)) {
            throw new IllegalStateException("티켓 소유자가 아닙니다.");
        }
        if (!isPendingAvailable()) {
            throw new IllegalStateException("결제할 수 없는 티켓입니다.");
        }
        this.status = TicketStatus.RESERVED;
        this.reservedAt = LocalDateTime.now();
    }

    // 예매 ( 결제 x )
    public void holdForPayment(Long userId) {
        if (!isOwnedBy(userId)) {
            throw new IllegalStateException("티켓 소유자가 아닙니다.");
        }
        if (!isPendingAvailable()) {
            throw new IllegalStateException("선점 상태가 아닙니다.");
        }

        this.status = TicketStatus.HELD;
        this.heldAt = LocalDateTime.now();
    }

    // 결제 확정
    public void confirmPayment(Long userId) {
        if (!isOwnedBy(userId)) {
            throw new IllegalStateException("티켓 소유자가 아닙니다.");
        }
        if (!isHeld()) {
            throw new IllegalStateException("입금 대기 상태가 아닙니다.");
        }

        this.status = TicketStatus.RESERVED;
        this.reservedAt = LocalDateTime.now();
    }

    // 선점 상태 취소
    public void cancelPending(Long userId) {
        if (!isOwnedBy(userId)) {
            throw new IllegalStateException("티켓 소유자가 아닙니다.");
        }
        if (!isPendingAvailable()) {
            throw new IllegalStateException("취소할 수 없는 상태입니다.");
        }
        this.status = TicketStatus.AVAILABLE;
        this.userId = null;
        this.reservedAt = null;
    }

    // 선점 상태 취소 ( 스케쥴러용 )
    public void release() {
        if (this.status != TicketStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태가 아닌 티켓은 해제할 수 없습니다.");
        }

        this.status = TicketStatus.AVAILABLE;
        this.userId = null;
        this.reservedAt = null;
    }

    // 입금 대기 상태 취소 ( 스케쥴러 용 )
    public void cancelHeld() {
        if (!isHeld()) {
            throw new IllegalStateException("HELD 상태가 아닙니다.");
        }

        this.status = TicketStatus.CANCELED;
        this.userId = null;
        this.heldAt = null;
    }

    // 결제 상태 취소
    public void cancelReserved(Long userId) {
        if (!isOwnedBy(userId)) {
            throw new IllegalStateException("티켓 소유자가 아닙니다.");
        }
        if (!isReserved()) {
            throw new IllegalStateException("취소할 수 없는 상태입니다.");
        }
        this.status = TicketStatus.CANCELED;
    }

    // 취소 후 다시 사용 가능하도록 상태 복구
    public void makeAvailableAgain() {
        if (!isCancelled()) {
            throw new IllegalStateException("AVAILABLE 상태로 복구할 수 없는 티켓입니다.");
        }
        this.status = TicketStatus.AVAILABLE;
        this.userId = null;
        this.pendingAt = null;
        this.reservedAt = null;
    }

    // 테스트용 티켓 초기화
    public void initTicket() {
        this.status = TicketStatus.AVAILABLE;
        this.userId = null;
        this.pendingAt = null;
        this.reservedAt = null;
    }

}
