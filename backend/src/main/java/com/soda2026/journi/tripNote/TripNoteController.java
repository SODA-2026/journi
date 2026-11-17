 package com.soda2026.journi.tripNote;

 import com.soda2026.journi.security.UserPrincipal;
 import com.soda2026.journi.tripNote.Dto.TripNoteRequestDto;
 import com.soda2026.journi.tripNote.Dto.TripNoteResponseDto;
 import com.soda2026.journi.tripNote.Dto.TripNoteSummeryDto;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
 import org.springframework.security.core.annotation.AuthenticationPrincipal;
 import org.springframework.web.bind.annotation.*;

 import java.util.List;

 @RestController
 @RequestMapping("/trip-notes")
 public class TripNoteController {

     private final TripNoteService tripNoteService;

     public TripNoteController(TripNoteService tripNoteService) {
         this.tripNoteService = tripNoteService;
     }

     @PostMapping
     public ResponseEntity<TripNoteResponseDto> createTripNote(@RequestBody TripNoteRequestDto tripNoteRequestDto, @AuthenticationPrincipal UserPrincipal user) {
         return ResponseEntity.status(HttpStatus.CREATED).body(tripNoteService.createTripNote(tripNoteRequestDto, user.getId()));

     }

     @GetMapping("/{tripId}")
     public ResponseEntity<List<TripNoteSummeryDto>> getTripNotesPerTrip(@PathVariable Long tripId, @AuthenticationPrincipal UserPrincipal user) {

         return ResponseEntity.ok(tripNoteService.getAllTripNotesByTripId(tripId, user.getId()));

     }

     @DeleteMapping("/{noteId}")
     public ResponseEntity<Void> deleteTripNote(@PathVariable Long noteId, @AuthenticationPrincipal UserPrincipal user) {
         tripNoteService.delete(noteId, user.getId());
         return ResponseEntity.noContent().build();
     }
 }
