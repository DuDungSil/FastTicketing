package org.example.ticket.domain.enums;

public enum TicketStatus {
    AVAILABLE, // 예약 가능
    RESERVED, // 예약됨 (PENDING + HELD + RESERVED 통합)
    BLOCKED, // 사용 불가 (CANCELED, 관리자 차단 등)
    CANCEL_PENDING // 취소 대기 ( 취소 표 )
}
