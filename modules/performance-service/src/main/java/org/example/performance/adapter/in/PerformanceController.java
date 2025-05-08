package org.example.performance.adapter.in;

import org.example.performance.adapter.in.request.PerformanceRequest;
import org.example.performance.adapter.in.request.ScheduleRequest;
import org.example.performance.application.service.PerformanceService;
import org.example.performance.application.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/performances")
public class PerformanceController {

    private final PerformanceService performanceService;
    private final ScheduleService scheduleService;

    // 공연 생성 ( admin )
    @PostMapping
    public ResponseEntity<String> createPerformance(@RequestBody PerformanceRequest request) {
        performanceService.createPerformance(request.title());
        return ResponseEntity.ok("new performance created.");
    }

    // 공연 스케쥴 생성 ( admin )
    @PostMapping("{performanceId}/schedule")
    public ResponseEntity<String> createPerformancSchedule(
            @PathVariable("performanceId") Integer performanceId,
            @RequestBody ScheduleRequest request) {
        scheduleService.createSchedule(performanceId, request.hallId(), request.startTime(), request.endTime());
        return ResponseEntity.ok("new performance schedule created.");
    }

}
