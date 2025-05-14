package org.example.performance.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.example.performance.adapter.out.HallRepository;
import org.example.performance.adapter.out.PerformanceRepository;
import org.example.performance.adapter.out.PerformanceScheduleRepository;
import org.example.performance.application.dto.ScheduleDetailDto;
import org.example.performance.application.dto.ScheduleDto;
import org.example.performance.domain.entity.Hall;
import org.example.performance.domain.entity.Performance;
import org.example.performance.domain.entity.PerformanceSchedule;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final PerformanceRepository performanceRepository;
    private final HallRepository hallRepository;
    private final PerformanceScheduleRepository performanceScheduleRepository;

    // 스케쥴 생성
    public void createSchedule(Integer peformanceId, Integer hallId, LocalDateTime startTime, LocalDateTime endTime) {
        Performance performance = performanceRepository.getReferenceById(peformanceId);

        Hall hall = hallRepository.getReferenceById(hallId);

        PerformanceSchedule performanceSchedule = new PerformanceSchedule(performance, hall, startTime, endTime);

        performanceScheduleRepository.save(performanceSchedule);
    }

    public List<ScheduleDto> getSchedulesByPerformance(Integer performanceId) {
        Performance performance = performanceRepository.findById(performanceId).orElseThrow();
        return performance.getSchedules().stream()
                .map(ScheduleDto::from)
                .toList();
    }

    public List<ScheduleDetailDto> getAllSchedulesDetails() {
        List<PerformanceSchedule> schedules = performanceScheduleRepository.findAllWithPerformanceAndHall();
        List<ScheduleDetailDto> dtos = schedules.stream()
                .map(ScheduleDetailDto::from)
                .toList();

        return dtos;
    }

    public void deleteSchedule(Integer id) {
        // 성능을 위해 직접 레포지토리에서 삭제
        performanceScheduleRepository.deleteById(id);
    }

}
