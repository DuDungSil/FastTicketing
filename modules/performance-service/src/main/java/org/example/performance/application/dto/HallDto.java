package org.example.performance.application.dto;

import org.example.performance.domain.entity.Hall;

public record HallDto(
        Integer id,
        String name
        ) {

    public static HallDto from(Hall hall) {
        return new HallDto(hall.getId(), hall.getName());
    }
}
