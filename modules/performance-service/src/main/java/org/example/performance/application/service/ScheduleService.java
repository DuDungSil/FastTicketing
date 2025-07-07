package org.example.performance.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.example.performance.adapter.out.HallRepository;
import org.example.performance.adapter.out.PerformanceRepository;
import org.example.performance.adapter.out.PerformanceScheduleRepository;
import org.example.performance.application.dto.ScheduleDetailDto;
import org.example.performance.application.dto.ScheduleDto;
import org.example.performance.application.dto.SeatDto;
import org.example.performance.domain.entity.Hall;
import org.example.performance.domain.entity.Performance;
import org.example.performance.domain.entity.PerformanceSchedule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final PerformanceRepository performanceRepository;
    private final HallRepository hallRepository;
    private final PerformanceScheduleRepository performanceScheduleRepository;

    // 스케쥴 생성
    public void createSchedule(Integer peformanceId, Integer hallId, LocalDateTime startTime, LocalDateTime endTime, int price) {
        Performance performance = performanceRepository.getReferenceById(peformanceId);

        Hall hall = hallRepository.getReferenceById(hallId);

        PerformanceSchedule performanceSchedule = new PerformanceSchedule(performance, hall, startTime, endTime, price);

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

    /**
     * 스케줄 ID로 해당 홀의 좌석 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SeatDto> getSeatsByScheduleId(Integer scheduleId) {
        PerformanceSchedule schedule = performanceScheduleRepository.findByIdWithSeats(scheduleId);
        if (schedule == null) {
            throw new IllegalArgumentException("존재하지 않는 스케줄입니다.");
        }
        
        Hall hall = schedule.getHall();
        return hall.getSeats().stream()
                .map(seat -> new SeatDto(seat.getId(), seat.getRowIndex(), seat.getColumnIndex()))
                .toList();
    }

}
