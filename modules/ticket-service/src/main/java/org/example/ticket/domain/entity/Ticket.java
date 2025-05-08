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

    private LocalDateTime reservedAt;

    private LocalDateTime paidAt;

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

    // 취소 상태 여부
    public boolean isCancelled() {
        return this.status == TicketStatus.CANCELLED;
    }

    // 예매
    public void reserve(Long userId) {
        if (!isAvailable()) {
            throw new IllegalStateException("예매할 수 없는 티켓입니다.");
        }
        this.status = TicketStatus.PENDING;
        this.userId = userId;
        this.reservedAt = LocalDateTime.now();
    }

    // 결제
    public void pay(Long userId) {
        if (!isOwnedBy(userId)) {
            throw new IllegalStateException("티켓 소유자가 아닙니다.");
        }
        if (!isPendingAvailable()) {
            throw new IllegalStateException("결제할 수 없는 티켓입니다.");
        }
        this.status = TicketStatus.RESERVED;
        this.paidAt = LocalDateTime.now();
    }

    // 펜딩 상태 취소
    public void cancelPending(Long userId) {
        if (!isOwnedBy(userId)) {
            throw new IllegalStateException("티켓 소유자가 아닙니다.");
        }
        if (!isPendingAvailable()) {
            throw new IllegalStateException("취소할 수 없는 상태입니다.");
        }
        this.status = TicketStatus.CANCELLED;
    }

    // 결제 상태 취소
    public void cancelReserved(Long userId) {
        if (isOwnedBy(userId)) {
            throw new IllegalStateException("티켓 소유자가 아닙니다.");
        }
        if (!isReserved()) {
            throw new IllegalStateException("취소할 수 없는 상태입니다.");
        }
        this.status = TicketStatus.CANCELLED;
    }

    // 취소 후 다시 사용 가능하도록 상태 복구
    public void makeAvailableAgain() {
        if (!isCancelled()) {
            throw new IllegalStateException("AVAILABLE 상태로 복구할 수 없는 티켓입니다.");
        }
        this.status = TicketStatus.AVAILABLE;
        this.userId = null;
        this.reservedAt = null;
        this.paidAt = null;
    }

    // 테스트용 티켓 초기화
    public void initTicket() {
        this.status = TicketStatus.AVAILABLE;
        this.userId = null;
        this.reservedAt = null;
        this.paidAt = null;
    }

}
