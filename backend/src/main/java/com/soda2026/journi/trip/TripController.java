package com.soda2026.journi.trip;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.security.Principal;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping({"/trips", "/api/trips"})
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    public ResponseEntity<List<TripResponseDTO>> getAll() {
        return
                ResponseEntity.ok(tripService.getAll());
    }

    @PostMapping
    public ResponseEntity<CreatedTripResponse> create(
            @Valid @RequestBody CreateTripRequest request, Principal principal) {
        var trip = tripService.create(request, principal);
        return ResponseEntity.status(201).body(trip);
    }
}
