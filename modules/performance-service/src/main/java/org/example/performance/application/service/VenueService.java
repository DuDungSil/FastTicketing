package org.example.performance.application.service;

import java.util.List;

import org.example.performance.adapter.out.VenueRepository;
import org.example.performance.application.dto.VenueDto;
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

    public List<VenueDto> getAllVenues() {
        List<Venue> venues = venueRepository.findAll();
        return venues.stream()
                .map(VenueDto::from)
                .toList();
    }

    public void deleteVenue(Integer id) {
        venueRepository.deleteById(id);
    }

}
