package org.example.ticket.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.example.ticket.domain.entity.TicketOpen;
import org.example.ticket.domain.enums.OpenType;

public record TicketOpenDto(
        Integer id,
        Integer scheduleId,
        LocalDateTime openAt,
        Integer limitPerUser,
        OpenType openType
        ) {

    public static TicketOpenDto from(TicketOpen entity) {
        return new TicketOpenDto(
                entity.getId(),
                entity.getScheduleId(),
                entity.getOpenAt(),
                entity.getLimitPerUser(),
                entity.getOpenType()
        );
    }

    public static List<TicketOpenDto> fromList(List<TicketOpen> entities) {
        return entities.stream()
                .map(TicketOpenDto::from)
                .collect(Collectors.toList());
    }
}
