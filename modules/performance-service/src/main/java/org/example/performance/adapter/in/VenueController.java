package org.example.performance.adapter.in;

import org.example.performance.adapter.in.request.HallRequest;
import org.example.performance.adapter.in.request.ScheduleRequest;
import org.example.performance.adapter.in.request.VenueRequest;
import org.example.performance.application.service.HallService;
import org.example.performance.application.service.VenueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/venues")
public class VenueController {

    private final VenueService venueService;
    private final HallService hallService;

    // 장소 정보 등록 ( admin )
    @PostMapping
    public ResponseEntity<String> createVenues(@RequestBody VenueRequest request) {
        venueService.createVenue(request.name());
        return ResponseEntity.ok("new Venue created.");
    }

    // 홀 정보 등록 ( admin )
    @PostMapping("{venueId}/hall")
    public ResponseEntity<String> createHall(@PathVariable("venueId") Integer venueId, @RequestBody HallRequest request) {
        hallService.createHall(venueId, request.name(), request.row(), request.column());
        return ResponseEntity.ok("new performance schedule created.");
    }

}
