package org.example.performance.application.dto;

import org.example.performance.domain.entity.Venue;

public record VenueDto(
        Integer id,
        String name
        ) {

    public static VenueDto from(Venue venue) {
        return new VenueDto(venue.getId(), venue.getName());
    }
}
