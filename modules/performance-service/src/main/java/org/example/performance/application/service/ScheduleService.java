package org.example.performance.application.service;

import java.time.LocalDateTime;

import org.example.performance.adapter.out.HallRepository;
import org.example.performance.adapter.out.PerformanceRepository;
import org.example.performance.adapter.out.PerformanceScheduleRepository;
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

}
