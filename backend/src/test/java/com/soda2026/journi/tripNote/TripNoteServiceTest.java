package com.soda2026.journi.tripNote;

import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.trip.TripRepository;
import com.soda2026.journi.tripMember.TripMemberRepository;
import com.soda2026.journi.tripNote.Dto.TripNoteRequestDto;
import com.soda2026.journi.user.UserModel;
import com.soda2026.journi.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TripNoteServiceTest {
    private final TripNoteRepository notes = mock(TripNoteRepository.class);
    private final TripRepository trips = mock(TripRepository.class);
    private final UserRepository users = mock(UserRepository.class);
    private final TripMemberRepository members = mock(TripMemberRepository.class);
    private final TripNoteService service =
            new TripNoteService(notes, trips, users, members, new TripNoteMapper());

    @Test
    void ownerCreatesNoteAndIsStoredAsAuthor() {
        TripModel trip = existingTrip();
        UserModel owner = user(1L);
        when(users.getReferenceById(1L)).thenReturn(owner);
        when(notes.save(any())).thenAnswer(call -> call.getArgument(0));

        var response = service.createTripNote(10L, request("Pack bags"), 1L);

        var captor = ArgumentCaptor.forClass(TripNoteModel.class);
        verify(notes).save(captor.capture());
        assertSame(trip, captor.getValue().getTrip());
        assertSame(owner, captor.getValue().getAuthor());
        assertEquals("Pack bags", captor.getValue().getContent());
        assertEquals(10L, response.getTripId());
        assertEquals(1L, response.getAuthorId());
        assertEquals("Pack bags", response.getContent());
        assertNotNull(response.getCreatedAt());
        verify(members, never()).existsById_TripIdAndId_UserId(anyLong(), anyLong());
    }

    @Test
    void tripMemberCreatesNote() {
        existingTrip();
        UserModel member = user(2L);
        when(members.existsById_TripIdAndId_UserId(10L, 2L)).thenReturn(true);
        when(users.getReferenceById(2L)).thenReturn(member);
        when(notes.save(any())).thenAnswer(call -> call.getArgument(0));

        var response = service.createTripNote(10L, request("Meet at 9"), 2L);

        assertEquals(2L, response.getAuthorId());
        verify(notes).save(any());
    }

    @Test
    void outsiderCannotCreateNote() {
        existingTrip();
        when(members.existsById_TripIdAndId_UserId(10L, 3L)).thenReturn(false);

        assertError(HttpStatus.FORBIDDEN, "Not a member of this trip",
                () -> service.createTripNote(10L, request("Sneaky"), 3L));

        verify(notes, never()).save(any());
        verify(users, never()).getReferenceById(anyLong());
    }

    @Test
    void cannotCreateNoteForMissingTrip() {
        assertError(HttpStatus.NOT_FOUND, "Trip not found",
                () -> service.createTripNote(10L, request("Lost"), 1L));

        verify(notes, never()).save(any());
        verify(users, never()).getReferenceById(anyLong());
    }

    @Test
    void listsNotesInRepositoryOrderForOwner() {
        TripModel trip = existingTrip();
        List<TripNoteModel> stored = List.of(
                new TripNoteModel(trip, user(1L), "Newest"),
                new TripNoteModel(trip, user(2L), "Older"));
        when(notes.findAllByTrip_IdOrderByUpdatedAtDesc(10L)).thenReturn(stored);

        var result = service.getAllTripNotesByTripId(10L, 1L);

        assertEquals(2, result.size());
        assertEquals("Newest", result.get(0).content());
        assertEquals("Older", result.get(1).content());
        verify(notes).findAllByTrip_IdOrderByUpdatedAtDesc(10L);
        verify(members, never()).existsById_TripIdAndId_UserId(anyLong(), anyLong());
    }

    @Test
    void listsNotesForTripMember() {
        TripModel trip = existingTrip();
        when(members.existsById_TripIdAndId_UserId(10L, 2L)).thenReturn(true);
        List<TripNoteModel> stored = List.of(new TripNoteModel(trip, user(1L), "Note"));
        when(notes.findAllByTrip_IdOrderByUpdatedAtDesc(10L)).thenReturn(stored);

        var result = service.getAllTripNotesByTripId(10L, 2L);

        assertEquals(1, result.size());
        assertEquals("Note", result.getFirst().content());
    }

    @Test
    void outsiderCannotListNotes() {
        existingTrip();
        when(members.existsById_TripIdAndId_UserId(10L, 3L)).thenReturn(false);

        assertError(HttpStatus.FORBIDDEN, "Not a member of this trip",
                () -> service.getAllTripNotesByTripId(10L, 3L));

        verify(notes, never()).findAllByTrip_IdOrderByUpdatedAtDesc(anyLong());
    }

    @Test
    void cannotListNotesForMissingTrip() {
        assertError(HttpStatus.NOT_FOUND, "Trip not found",
                () -> service.getAllTripNotesByTripId(10L, 1L));

        verify(notes, never()).findAllByTrip_IdOrderByUpdatedAtDesc(anyLong());
    }

    @Test
    void authorCanDeleteOwnNote() {
        TripModel trip = existingTrip();
        TripNoteModel note = new TripNoteModel(trip, user(2L), "Mine");
        when(notes.findById(5L)).thenReturn(Optional.of(note));
        when(members.existsById_TripIdAndId_UserId(10L, 2L)).thenReturn(true);

        service.delete(10L, 5L, 2L);

        verify(notes).delete(note);
    }

    @Test
    void tripOwnerCanDeleteAnyNote() {
        TripModel trip = existingTrip();
        TripNoteModel note = new TripNoteModel(trip, user(2L), "Someone else's");
        when(notes.findById(5L)).thenReturn(Optional.of(note));

        service.delete(10L, 5L, 1L);

        verify(notes).delete(note);
        verify(members, never()).existsById_TripIdAndId_UserId(anyLong(), anyLong());
    }

    @Test
    void otherMemberCannotDeleteAnotherAuthorsNote() {
        TripModel trip = existingTrip();
        TripNoteModel note = new TripNoteModel(trip, user(2L), "Not yours");
        when(notes.findById(5L)).thenReturn(Optional.of(note));
        when(members.existsById_TripIdAndId_UserId(10L, 3L)).thenReturn(true);

        assertError(HttpStatus.FORBIDDEN, "Not allowed to delete this note",
                () -> service.delete(10L, 5L, 3L));

        verify(notes, never()).delete(any());
    }

    @Test
    void removedAuthorCannotDeleteOwnNote() {
        TripModel trip = existingTrip();
        TripNoteModel note = new TripNoteModel(trip, user(2L), "Was mine");
        when(notes.findById(5L)).thenReturn(Optional.of(note));
        when(members.existsById_TripIdAndId_UserId(10L, 2L)).thenReturn(false);

        assertError(HttpStatus.FORBIDDEN, "Not a member of this trip",
                () -> service.delete(10L, 5L, 2L));

        verify(notes, never()).delete(any());
    }

    @Test
    void cannotDeleteMissingNote() {
        assertError(HttpStatus.NOT_FOUND, "Trip note not found",
                () -> service.delete(10L, 5L, 1L));

        verify(notes, never()).delete(any());
    }

    @Test
    void cannotDeleteNoteThroughAnotherTrip() {
        TripModel otherTrip = new TripModel();
        otherTrip.setId(20L);
        otherTrip.setOwnerId(1L);
        TripNoteModel note = new TripNoteModel(otherTrip, user(1L), "Other trip");
        when(notes.findById(5L)).thenReturn(Optional.of(note));

        assertError(HttpStatus.NOT_FOUND, "Trip note not found",
                () -> service.delete(10L, 5L, 1L));

        verify(notes, never()).delete(any());
    }

    private TripModel existingTrip() {
        TripModel trip = new TripModel();
        trip.setId(10L);
        trip.setOwnerId(1L);
        when(trips.findById(10L)).thenReturn(Optional.of(trip));
        return trip;
    }

    private UserModel user(Long id) {
        UserModel user = new UserModel();
        user.setId(id);
        return user;
    }

    private TripNoteRequestDto request(String content) {
        TripNoteRequestDto request = new TripNoteRequestDto();
        request.setContent(content);
        return request;
    }

    private void assertError(HttpStatus status, String reason,
                             org.junit.jupiter.api.function.Executable action) {
        var error = assertThrows(ResponseStatusException.class, action);
        assertEquals(status, error.getStatusCode());
        assertEquals(reason, error.getReason());
    }
}
