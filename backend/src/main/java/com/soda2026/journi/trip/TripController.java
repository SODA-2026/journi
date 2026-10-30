package com.soda2026.journi.trip;

import com.soda2026.journi.trip.dto.CreateTripRequest;
import com.soda2026.journi.trip.dto.TripResponse;
import com.soda2026.journi.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<TripResponse> createTrip(@RequestBody CreateTripRequest createTripRequest) {
//        1L = 1 Long
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.createTrip(createTripRequest, 1L));
    }

}
