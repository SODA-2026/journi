package com.soda2026.journi.tripNote;

import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.user.UserModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TripNoteMapperTest {
    private final TripNoteMapper mapper = new TripNoteMapper();

    @Test
    void mapsAuthorNameInResponse() {
        var dto = mapper.toDto(new TripNoteModel(trip(), user("Sam"), "Hello"));

        assertEquals(1L, dto.getAuthorId());
        assertEquals("Sam", dto.getAuthorName());
    }

    @Test
    void mapsAuthorNameInSummary() {
        var dto = mapper.toTripNoteSummaryDto(new TripNoteModel(trip(), user("Sam"), "Hello"));

        assertEquals(1L, dto.authorId());
        assertEquals("Sam", dto.authorName());
    }

    @Test
    void mapsDeletedAuthorToNullIdInResponse() {
        TripNoteModel note = noteWithDeletedAuthor();

        var dto = mapper.toDto(note);

        assertNull(dto.getAuthorId());
        assertNull(dto.getAuthorName());
        assertEquals(10L, dto.getTripId());
        assertEquals("Orphaned", dto.getContent());
    }

    @Test
    void mapsDeletedAuthorToNullIdInSummary() {
        TripNoteModel note = noteWithDeletedAuthor();

        var dto = mapper.toTripNoteSummaryDto(note);

        assertNull(dto.authorId());
        assertNull(dto.authorName());
        assertEquals("Orphaned", dto.content());
    }

    private TripModel trip() {
        TripModel trip = new TripModel();
        trip.setId(10L);
        return trip;
    }

    private UserModel user(String name) {
        UserModel user = new UserModel();
        user.setId(1L);
        user.setName(name);
        return user;
    }

    private TripNoteModel noteWithDeletedAuthor() {
        return new TripNoteModel(trip(), null, "Orphaned");
    }
}
