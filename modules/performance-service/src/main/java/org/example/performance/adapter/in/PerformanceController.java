package org.example.performance.adapter.in;

import java.util.List;

import org.example.performance.adapter.in.request.PerformanceRequest;
import org.example.performance.adapter.in.request.ScheduleRequest;
import org.example.performance.application.dto.PerformanceDto;
import org.example.performance.application.dto.ScheduleDetailDto;
import org.example.performance.application.dto.ScheduleDto;
import org.example.performance.application.dto.SeatDto;
import org.example.performance.application.service.PerformanceService;
import org.example.performance.application.service.ScheduleService;
import org.example.performance.domain.entity.PerformanceSchedule;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/performances")
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
        scheduleService.createSchedule(performanceId, request.hallId(), request.startTime(), request.endTime(), request.price());
        return ResponseEntity.ok("new performance schedule created.");
    }

    @GetMapping
    public ResponseEntity<List<PerformanceDto>> getAll() {
        List<PerformanceDto> performanceDtoList = performanceService.getAllPerformances();
        return ResponseEntity.ok(performanceDtoList);
    }

    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<ScheduleDto>> getSchedules(@PathVariable Integer id) {
        List<ScheduleDto> scheduleDtoList = scheduleService.getSchedulesByPerformance(id);
        return ResponseEntity.ok(scheduleDtoList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerformance(@PathVariable Integer id) {
        performanceService.deletePerformance(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/schedules/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Integer scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/schedules")
    public ResponseEntity<List<ScheduleDetailDto>> getAllSchedulesDetails() {
        List<ScheduleDetailDto> dtos = scheduleService.getAllSchedulesDetails();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/schedules/{scheduleId}/seats")
    public ResponseEntity<List<SeatDto>> getSeatsBySchedule(@PathVariable Integer scheduleId) {
        List<SeatDto> seats = scheduleService.getSeatsByScheduleId(scheduleId);
        return ResponseEntity.ok(seats);
    }

}
