package org.example.performance.application.dto;

import org.example.performance.domain.entity.Performance;

public record PerformanceDto(
        Integer id,
        String title
        ) {

    public static PerformanceDto from(Performance performance) {
        return new PerformanceDto(performance.getId(), performance.getTitle());
    }
}
