package org.example.performance.application.service;

import java.util.List;

import org.example.performance.adapter.out.HallRepository;
import org.example.performance.adapter.out.VenueRepository;
import org.example.performance.domain.entity.Hall;
import org.example.performance.domain.entity.Seat;
import org.example.performance.domain.entity.Venue;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class HallService {

    private final VenueRepository venueRepository;
    private final HallRepository hallRepository;
    private final SeatsGenerateService seatsGenerateService;

    // Hall 정보 생성
    public void createHall(Integer venueId, String name, int row, int column) {
        // venue 프록시 객체
        Venue venue = venueRepository.getReferenceById(venueId);

        // Seat 생성
        List<Seat> seats = seatsGenerateService.generateSeats(row, column);

        Hall newHall = new Hall(name, venue, seats);

        hallRepository.save(newHall);
    }

}
