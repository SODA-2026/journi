 package com.soda2026.journi.tripActivity;

 import com.soda2026.journi.security.UserPrincipal;
 import com.soda2026.journi.tripActivity.dto.AddPlaceActivityDTO;
 import com.soda2026.journi.tripActivity.dto.TripActivityRequestDTO;
 import com.soda2026.journi.tripActivity.dto.TripActivityResponseDTO;
 import jakarta.validation.Valid;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
 import org.springframework.security.core.annotation.AuthenticationPrincipal;
 import org.springframework.web.bind.annotation.*;
 import com.soda2026.journi.tripActivity.TripActivityService;

 import java.util.List;

 @RestController
 @RequestMapping("api/activities")
 public class TripActivityController {
     private final TripActivityService tripActivityService;


     public TripActivityController(TripActivityService tripActivityService) {
         this.tripActivityService = tripActivityService;

     }

     @GetMapping
     public List<TripActivityResponseDTO> getAll() {
         return tripActivityService.findAll();
     }

     @GetMapping("/{id}")
     public TripActivityResponseDTO getById(@PathVariable Long id) {
         return tripActivityService.findById(id);
     }

     @DeleteMapping("/{id}")
     @ResponseStatus(HttpStatus.NO_CONTENT)
     public void delete(@PathVariable Long id) {
         tripActivityService.delete(id);
     }

     @GetMapping("/trips/{tripId}")
     public List<TripActivityResponseDTO> getActivities(@PathVariable Long tripId,
                                                        @RequestParam Long tripDestinationId){
         return tripActivityService.getActivityForDestination(tripId, tripDestinationId);
     }

     @PostMapping("/trips/{tripId}")
     public ResponseEntity<TripActivityResponseDTO> create(@PathVariable Long tripId,
                                                           @Valid @RequestBody TripActivityRequestDTO request,
                                                           @AuthenticationPrincipal UserPrincipal user){
         return ResponseEntity.status(HttpStatus.CREATED)
                 .body(tripActivityService.createActivity(tripId, request, user.getId()));
     }

     @PostMapping("/trips/{tripId}/places")
     @ResponseStatus(HttpStatus.NO_CONTENT)
     public void addPlaceActivity(
             @PathVariable Long tripId,
             @RequestBody AddPlaceActivityDTO request,
             @AuthenticationPrincipal UserPrincipal user
     ) {
         tripActivityService.addPlaceToActivity(
                 tripId,
                 request,
                 user.getId()
         );
     }


 }
