package org.example.performance.adapter.out;

import java.util.List;

import org.example.performance.domain.entity.Hall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HallRepository extends JpaRepository<Hall, Integer> {

    List<Hall> findByVenueId(Integer venueId);

}
