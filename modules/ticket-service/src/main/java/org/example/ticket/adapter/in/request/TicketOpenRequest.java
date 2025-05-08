package org.example.ticket.adapter.in.request;

import java.time.LocalDateTime;

import org.example.ticket.domain.enums.OpenType;

public record TicketOpenRequest(
        Integer scheduleId,
        LocalDateTime openAt,
        Integer limitPerUser,
        OpenType openType) {

}
