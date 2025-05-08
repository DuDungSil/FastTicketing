package org.example.performance.application.service;

import org.example.performance.adapter.out.PerformanceRepository;
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

}
