package org.example.performance.application.dto;

import java.time.LocalDateTime;

import org.example.performance.domain.entity.PerformanceSchedule;

public record ScheduleDto(
        Integer id,
        Integer hallId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int price
        ) {

    public static ScheduleDto from(PerformanceSchedule schedule) {
        return new ScheduleDto(
                schedule.getId(),
                schedule.getHall().getId(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getPrice()
        );
    }
}
