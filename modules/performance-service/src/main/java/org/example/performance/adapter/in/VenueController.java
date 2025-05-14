package org.example.performance.adapter.in;

import java.util.List;

import org.example.performance.adapter.in.request.HallRequest;
import org.example.performance.adapter.in.request.VenueRequest;
import org.example.performance.application.dto.HallDto;
import org.example.performance.application.dto.VenueDto;
import org.example.performance.application.service.HallService;
import org.example.performance.application.service.VenueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/venues")
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
    public ResponseEntity<String> createHall(@PathVariable("venueId") Integer venueId,
            @RequestBody HallRequest request) {
        hallService.createHall(venueId, request.name(), request.row(), request.column());
        return ResponseEntity.ok("new performance schedule created.");
    }

    @GetMapping
    public ResponseEntity<List<VenueDto>> getVenues() {
        List<VenueDto> venueDtoList = venueService.getAllVenues();
        return ResponseEntity.ok(venueDtoList);
    }

    @GetMapping("/{venueId}/halls")
    public ResponseEntity<List<HallDto>> getHalls(@PathVariable Integer venueId) {
        List<HallDto> hallDtoList = hallService.getHallsByVenue(venueId);
        return ResponseEntity.ok(hallDtoList);
    }

    @DeleteMapping("/{venueId}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Integer venueId) {
        venueService.deleteVenue(venueId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{venueId}/halls/{hallId}")
    public ResponseEntity<Void> deleteHall(@PathVariable Integer hallId) {
        hallService.deleteHall(hallId);
        return ResponseEntity.noContent().build();
    }

}
