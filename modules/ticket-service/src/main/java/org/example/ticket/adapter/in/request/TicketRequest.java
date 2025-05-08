package org.example.ticket.adapter.in.request;

import java.util.List;

public record TicketRequest(
        Long userId,
        List<Long> ticketIds) {

}
