package org.example.ticket.application.dto;

import org.example.ticket.domain.enums.TicketStatus;

public record TicketStatusDto(
        Long ticketId,
        String seatCode,
        TicketStatus status) {

}
