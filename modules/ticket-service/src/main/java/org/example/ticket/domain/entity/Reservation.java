package org.example.ticket.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.ticket.domain.enums.ReservationStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 유저 예매 최종 이력
 */
@Getter
@NoArgsConstructor
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<Ticket> tickets = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime confirmedAt;
    private LocalDateTime canceledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    // 예약 유지 시간 (결제 대기 시간)
    private LocalDateTime expiresAt;

    // 현재 진행 중인 결제 트랜잭션 ID
    @Column(length = 100)
    private String currentPaymentId;

    // ===== 생성자 =====
    public Reservation(Long userId, List<Ticket> tickets) {
        this.userId = userId;
        this.tickets = tickets;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusMinutes(10);
        this.status = ReservationStatus.REQUESTED;

        for (Ticket ticket : tickets) {
            ticket.assignReservation(this);
            ticket.tryReserve(); // 좌석 상태 RESERVED
        }
    }

    // ===== 상태 전이 메서드 =====
    public void startPayment(String paymentId) {
        if (this.status != ReservationStatus.REQUESTED) {
            throw new IllegalStateException("요청 상태의 예약만 결제를 시작할 수 있습니다.");
        }
        this.currentPaymentId = paymentId;
        // 상태는 그대로 REQUESTED (결제 완료 시까지)
    }

    public void confirmPayment() {
        if (this.status != ReservationStatus.REQUESTED) {
            throw new IllegalStateException("요청 상태의 예약만 확정할 수 있습니다.");
        }
        this.status = ReservationStatus.COMPLETED;
        this.confirmedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == ReservationStatus.COMPLETED) {
            this.status = ReservationStatus.CANCELED;
            this.canceledAt = LocalDateTime.now();
            for (Ticket ticket : this.tickets) {
                ticket.markAsCancelPending(); // 결제 완료된 경우는 즉시 반환 안 함
            }
        } else if (this.status == ReservationStatus.REQUESTED) {
            this.status = ReservationStatus.CANCELED;
            this.canceledAt = LocalDateTime.now();
            for (Ticket ticket : this.tickets) {
                ticket.release(); // 결제 전이면 즉시 좌석 해제
            }
        } else {
            throw new IllegalStateException("이미 취소되었거나 완료된 예약만 취소할 수 있습니다.");
        }
    }

    public void expire() {
        if (this.status != ReservationStatus.REQUESTED) {
            return;
        }

        this.status = ReservationStatus.EXPIRED;
        this.canceledAt = LocalDateTime.now();

        for (Ticket ticket : this.tickets) {
            ticket.release();
        }
    }

    // ===== 상태 확인 메서드 =====
    public boolean isCompleted() {
        return this.status == ReservationStatus.COMPLETED;
    }

    public boolean isActive() {
        return this.status == ReservationStatus.REQUESTED;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public boolean hasPaymentInProgress() {
        return this.currentPaymentId != null && this.status == ReservationStatus.REQUESTED;
    }

}
