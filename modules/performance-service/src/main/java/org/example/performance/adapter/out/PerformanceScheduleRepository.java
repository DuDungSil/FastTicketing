package org.example.performance.adapter.out;

import java.util.List;

import org.example.performance.domain.entity.PerformanceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceScheduleRepository extends JpaRepository<PerformanceSchedule, Integer> {

    @Query("""
    SELECT ps FROM PerformanceSchedule ps
    JOIN FETCH ps.performance
    JOIN FETCH ps.hall h
    JOIN FETCH h.venue
    """)
    List<PerformanceSchedule> findAllWithPerformanceAndHall();

}
