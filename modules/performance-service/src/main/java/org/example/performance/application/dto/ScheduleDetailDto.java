package org.example.performance.application.dto;

import java.time.LocalDateTime;

import org.example.performance.domain.entity.PerformanceSchedule;

public record ScheduleDetailDto(
        Integer id,
        String performanceTitle,
        String venueName,
        String hallName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer price
        ) {

    public static ScheduleDetailDto from(PerformanceSchedule s) {
        return new ScheduleDetailDto(
                s.getId(),
                s.getPerformance().getTitle(),
                s.getHall().getVenue().getName(),
                s.getHall().getName(),
                s.getStartTime(),
                s.getEndTime(),
                s.getPrice()
        );
    }
}
