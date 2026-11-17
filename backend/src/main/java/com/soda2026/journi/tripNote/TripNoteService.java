package com.soda2026.journi.tripNote;

import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.trip.TripRepository;
import com.soda2026.journi.tripMember.TripMemberRepository;
import com.soda2026.journi.tripNote.Dto.TripNoteRequestDto;
import com.soda2026.journi.tripNote.Dto.TripNoteResponseDto;
import com.soda2026.journi.tripNote.Dto.TripNoteSummeryDto;
import com.soda2026.journi.user.UserModel;
import com.soda2026.journi.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TripNoteService {

    private final TripNoteRepository tripNoteRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripMemberRepository tripMemberRepository;
    private final TripNoteMapper mapper;

    public TripNoteService(TripNoteRepository tripNoteRepository,
                           TripRepository tripRepository,
                           UserRepository userRepository,
                           TripMemberRepository tripMemberRepository,
                           TripNoteMapper mapper) {
        this.tripNoteRepository = tripNoteRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.tripMemberRepository = tripMemberRepository;
        this.mapper = mapper;
    }

    @Transactional
    public TripNoteResponseDto createTripNote(Long tripId, TripNoteRequestDto dto, Long userId) {
        TripModel trip = getTripAndVerifyAccess(tripId, userId);

        UserModel author = userRepository.getReferenceById(userId);

        TripNoteModel saved = tripNoteRepository.save(mapper.toEntity(dto, trip, author));
        return mapper.toDto(saved);
    }

    public List<TripNoteSummeryDto> getAllTripNotesByTripId(Long tripId, Long userId) {
        // 1. Guard: Does the trip exist and is this user allowed to see it?
        getTripAndVerifyAccess(tripId, userId);

        // 2. Fetch: Get the raw TripNoteModel entities for this tripId from the DB
        List<TripNoteModel> notes = tripNoteRepository.findAllByTrip_IdOrderByUpdatedAtDesc(tripId);

        // 3. Transform: Convert each TripNoteModel into a TripNoteSummeryDto and return the list
        return notes.stream()
                .map(mapper::toTripNoteSummaryDto)
                .toList();
    }

    private TripModel getTripAndVerifyAccess(Long tripId, Long userId) {
        TripModel trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));

        boolean allowed = trip.getOwnerId().equals(userId)
                || tripMemberRepository.existsById_TripIdAndId_UserId(trip.getId(), userId);

        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member of this trip");
        }

        return trip;
    }

    @Transactional
    public void delete(Long tripId, Long noteId, Long currentUserId) {
        // 1. Find the note (404 if it doesn't exist)
        TripNoteModel note = tripNoteRepository.findById(noteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip note not found"));

        if (!note.getTrip().getId().equals(tripId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip note not found");
        }

        // 2. Check permission: allow the note's author OR the trip owner
        boolean isAuthor = note.getAuthor().getId().equals(currentUserId);
        boolean isTripOwner = note.getTrip().getOwnerId().equals(currentUserId);

        if (!isAuthor && !isTripOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to delete this note");
        }

        // 3. Delete the note
        tripNoteRepository.delete(note); // or tripNoteRepository.deleteById(noteId);
    }
}