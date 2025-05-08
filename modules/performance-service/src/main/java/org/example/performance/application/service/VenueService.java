package org.example.performance.application.service;

import org.example.performance.adapter.out.VenueRepository;

import org.example.performance.domain.entity.Venue;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class VenueService {

    private final VenueRepository venueRepository;

    public void createVenue(String name) {

        Venue venue = new Venue(name);

        venueRepository.save(venue);
    }

}
