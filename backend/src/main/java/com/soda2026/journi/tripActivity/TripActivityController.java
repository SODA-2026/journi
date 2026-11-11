 package com.soda2026.journi.tripActivity;

 import com.soda2026.journi.tripActivity.dto.TripActivityResponseDTO;
 import org.springframework.web.bind.annotation.*;
 import com.soda2026.journi.activity.TripActivityService;

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
     public void delete(@PathVariable Long id) {
         tripActivityService.delete(id);
     }

 }
