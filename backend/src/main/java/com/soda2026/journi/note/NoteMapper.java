package com.soda2026.journi.note;

import com.soda2026.journi.note.Dto.NoteRequestDto;
import com.soda2026.journi.note.Dto.NoteResponseDto;
import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.user.UserModel;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

    public NoteResponseDto toDto(NoteModel note){
        return new NoteResponseDto(
                note.getId(),
                note.getTrip().getId(),
                note.getAuthor().getId(),
                note.getContent(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }

    public NoteModel toEntity(NoteRequestDto dto, TripModel trip, UserModel author){
        return new NoteModel(trip, author, dto.getContent());
    }
}
