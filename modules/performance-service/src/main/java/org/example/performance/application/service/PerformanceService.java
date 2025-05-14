package org.example.performance.application.service;

import java.util.List;

import org.example.performance.adapter.out.PerformanceRepository;
import org.example.performance.application.dto.PerformanceDto;
import org.example.performance.domain.entity.Performance;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    // 공연 생성
    public void createPerformance(String title) {

        Performance performance = new Performance(title);

        // title 중복 체크 ?
        performanceRepository.save(performance);
    }

    public List<PerformanceDto> getAllPerformances() {
        return performanceRepository.findAll()
                .stream()
                .map(PerformanceDto::from)
                .toList();
    }

    public void deletePerformance(Integer id) {
        performanceRepository.deleteById(id);
    }

}
