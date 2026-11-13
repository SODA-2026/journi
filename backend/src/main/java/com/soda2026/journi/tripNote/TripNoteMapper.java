 package com.soda2026.journi.tripNote;

 import com.soda2026.journi.tripNote.Dto.TripNoteRequestDto;
 import com.soda2026.journi.tripNote.Dto.TripNoteResponseDto;
 import com.soda2026.journi.trip.TripModel;
 import com.soda2026.journi.user.UserModel;
 import org.springframework.stereotype.Component;

 @Component
 public class TripNoteMapper {

     public TripNoteResponseDto toDto(TripNoteModel note){
         return new TripNoteResponseDto(
                 note.getId(),
                 note.getTrip().getId(),
                 note.getAuthor().getId(),
                 note.getContent(),
                 note.getCreatedAt(),
                 note.getUpdatedAt()
         );
     }

     public TripNoteModel toEntity(TripNoteRequestDto dto, TripModel trip, UserModel author){
         return new TripNoteModel(trip, author, dto.getContent());
     }
 }
