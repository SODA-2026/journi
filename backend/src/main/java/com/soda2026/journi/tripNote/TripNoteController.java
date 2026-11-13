 package com.soda2026.journi.tripNote;

 import com.soda2026.journi.security.UserPrincipal;
 import com.soda2026.journi.tripNote.Dto.TripNoteRequestDto;
 import com.soda2026.journi.tripNote.Dto.TripNoteResponseDto;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
 import org.springframework.security.core.annotation.AuthenticationPrincipal;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RestController;

 @RestController
 @RequestMapping("/notes")
 public class TripNoteController {

     private final TripNoteService tripNoteService;

     public TripNoteController(TripNoteService tripNoteService) {
         this.tripNoteService = tripNoteService;
     }

     @PostMapping
     public ResponseEntity<TripNoteResponseDto> createTripNote(@RequestBody TripNoteRequestDto tripNoteRequestDto, @AuthenticationPrincipal UserPrincipal user) {
         return ResponseEntity.status(HttpStatus.CREATED).body(tripNoteService.createTripNote(tripNoteRequestDto, user.getId()));

     }
 }
