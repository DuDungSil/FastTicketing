package org.example.ticket.application.dto;

import java.util.List;

import org.example.ticket.domain.entity.Reservation;
import org.example.ticket.domain.entity.Ticket;
import org.example.ticket.domain.enums.ReservationStatus;

public record ReservationDto(
        Long id,
        Long userId,
        List<Ticket> tickets,
        ReservationStatus status
        ) {

    public static ReservationDto fromEntity(Reservation reservation) {
        return new ReservationDto(
                reservation.getId(),
                reservation.getUserId(),
                reservation.getTickets(),
                reservation.getStatus()
        );
    }

}
