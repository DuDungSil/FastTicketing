package org.example.ticket.domain.enums;

public enum TicketStatus {
    AVAILABLE, // 아무도 예매하지 않은 초기 상태
    PENDING, // 선점 상태
    HELD, // 예매 완료 ( 결제 대기 )
    RESERVED, // 예매 완료 ( 결제 성공 )
    CANCELED // 취소됨
}
