package org.example.performance.adapter.in.request;

import java.time.LocalDateTime;

public record ScheduleRequest(
        int hallId,
        LocalDateTime startTime,
        LocalDateTime endTime) {

}
