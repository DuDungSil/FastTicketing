package org.example.performance.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class PerformanceSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @ManyToOne
    @JoinColumn(name = "hall_id")
    private Hall hall;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer price;

    // 생성자
    public PerformanceSchedule(Performance performance, Hall hall, LocalDateTime startTime, LocalDateTime endTime, int price) {
        this.performance = performance;
        this.hall = hall;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
    }
}
