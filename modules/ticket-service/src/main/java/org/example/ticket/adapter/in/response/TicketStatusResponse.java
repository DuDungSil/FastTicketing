package org.example.ticket.adapter.in.response;

import java.util.List;

import org.example.ticket.application.dto.TicketStatusDto;

public record TicketStatusResponse(
        List<TicketStatusDto> tickets) {

}
