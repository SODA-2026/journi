 package com.soda2026.journi.trip;

 import com.soda2026.journi.trip.dto.*;
 import com.soda2026.journi.trip.dto.TripInfoDTO;
 import com.soda2026.journi.user.UserService;
 import com.soda2026.journi.trip.dto.TripResponse;
 import com.soda2026.journi.trip.dto.TripSummaryDTO;
 import jakarta.validation.Valid;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.*;

 import java.util.List;
 import com.soda2026.journi.security.UserPrincipal;
 import org.springframework.security.core.annotation.AuthenticationPrincipal;

 @RestController
 @RequestMapping("/trips")
 public class TripController {

     private final TripService tripService;

     public TripController(TripService tripService) {
         this.tripService = tripService;
     }

     @PostMapping("/create")
     public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody CreateTripRequest createTripRequest, @AuthenticationPrincipal UserPrincipal user) {
         return ResponseEntity.status(HttpStatus.CREATED).body(tripService.createTrip(createTripRequest, user.getId()));
     }

     @GetMapping("/mine")
     public ResponseEntity<List<TripSummaryDTO>> myTrips(@AuthenticationPrincipal UserPrincipal user) {
         return ResponseEntity.ok(tripService.findMyTrips(user.getId()));
     }

     @DeleteMapping("/{id}")
     public ResponseEntity<Void> deleteTrip(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) {
         tripService.delete(id, user.getId());
         return ResponseEntity.noContent().build();
     }

     @GetMapping("/mine/{tripId}")
     public ResponseEntity<TripInfoDTO> getTripInfo(@AuthenticationPrincipal UserPrincipal user, @PathVariable Long tripId){
         return ResponseEntity.ok(tripService.getTripInfo(tripId, user.getId()));
     }

     @PutMapping("/mine/{tripId}")
     public ResponseEntity<UpdateTripResponseDTO> updateTrip(@Valid @RequestBody UpdateTripRequestDTO dto, @AuthenticationPrincipal UserPrincipal user, @PathVariable Long tripId){
         return ResponseEntity.ok(tripService.updateTrip(tripId, user.getId(), dto));
     }

 }
