package com.soda2026.journi.tripNote;

import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.trip.TripRepository;
import com.soda2026.journi.tripMember.TripMemberRepository;
import com.soda2026.journi.tripNote.Dto.TripNoteRequestDto;
import com.soda2026.journi.tripNote.Dto.TripNoteResponseDto;
import com.soda2026.journi.user.UserModel;
import com.soda2026.journi.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public TripNoteResponseDto createTripNote(TripNoteRequestDto dto, Long userId) {
        TripModel trip = tripRepository.findById(dto.getTripId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));

        boolean allowed = trip.getOwnerId().equals(userId)
                || tripMemberRepository.existsById_TripIdAndId_UserId(trip.getId(), userId);

        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member of this trip");
        }

//        if (!tripMemberRepository.existsById_TripIdAndId_UserId(trip.getId(), userId)) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member of this trip");
//        }

        UserModel author = userRepository.getReferenceById(userId);

        TripNoteModel saved = tripNoteRepository.save(mapper.toEntity(dto, trip, author));
        return mapper.toDto(saved);
    }
}