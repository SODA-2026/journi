 package com.soda2026.journi.trip;

 import com.soda2026.journi.trip.dto.CreateTripRequest;
 import com.soda2026.journi.trip.dto.TripInfoDTO;
 import com.soda2026.journi.trip.dto.TripResponse;
 import com.soda2026.journi.trip.dto.TripSummaryDTO;
 import com.soda2026.journi.user.UserService;
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
     private final UserService userService;

     public TripController(TripService tripService, UserService userService) {
         this.tripService = tripService;
         this.userService = userService;
     }

     @GetMapping
     public ResponseEntity<List<TripSummaryDTO>> getAll(@AuthenticationPrincipal UserPrincipal user) {
         return ResponseEntity.ok(tripService.findTripsByOwnerId(user.getId()));
     }

     @PostMapping("/create")
     public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody CreateTripRequest createTripRequest, @AuthenticationPrincipal UserPrincipal user) {
         return ResponseEntity.status(HttpStatus.CREATED).body(tripService.createTrip(createTripRequest, user.getId()));
     }

     @GetMapping("/mine")
     public ResponseEntity<List<TripSummaryDTO>> myTrips(@AuthenticationPrincipal UserPrincipal user) {
         return ResponseEntity.ok(tripService.findTripsByOwnerId(user.getId()));
     }

     @DeleteMapping("/{id}")
     public ResponseEntity<Void> deleteTrip(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) {
         tripService.delete(id, user.getId());
         return ResponseEntity.noContent().build();
     }

     @GetMapping("/{ownerId}")
     public ResponseEntity<List<TripSummaryDTO>> findTripsByOwnerId(@PathVariable Long ownerId, @AuthenticationPrincipal UserPrincipal user){
         if (!ownerId.equals(user.getId())) {
             throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN);
         }
         return ResponseEntity.ok(tripService.findTripsByOwnerId(ownerId));
     }

     @GetMapping("/mine/{tripId}")
     public ResponseEntity<TripInfoDTO> getTripInfo(@AuthenticationPrincipal UserPrincipal user, @PathVariable Long tripId){
         return ResponseEntity.ok(tripService.getTripInfo(tripId, user.getId()));
     }

 }
