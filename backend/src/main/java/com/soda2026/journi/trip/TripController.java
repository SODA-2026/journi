package com.soda2026.journi.trip;

import com.soda2026.journi.trip.dto.CreateTripRequest;
import com.soda2026.journi.trip.dto.TripResponse;
import com.soda2026.journi.trip.dto.TripSummaryDTO;
import com.soda2026.journi.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trips")
public class TripController {

    private final TripService tripService;
    private final UserService userService;

    public TripController(TripService tripService, UserService userService) {
        this.tripService = tripService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<TripResponse>> getAll() {
        return
                ResponseEntity.ok(tripService.getAll());
    }

    @PostMapping("/create")
    public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody CreateTripRequest createTripRequest) {
//        1L = 1 Long
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.createTrip(createTripRequest, 1L));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        tripService.delete(id, 1L);
        return ResponseEntity.noContent().build();
    }

     @GetMapping("/{ownerId}")
     public ResponseEntity<List<TripSummaryDTO>> findTripsByOwnerId(@PathVariable Long ownerId){
         return ResponseEntity.ok(tripService.findTripsByOwnerId(ownerId));
     }

 }
