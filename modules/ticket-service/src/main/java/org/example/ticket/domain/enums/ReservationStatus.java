package org.example.ticket.domain.enums;

public enum ReservationStatus {
    REQUESTED, // 예약 요청 (결제 대기)
    COMPLETED, // 예약 완료 (결제 성공)
    CANCELED, // 사용자 취소
    EXPIRED       // 시간 만료
}
