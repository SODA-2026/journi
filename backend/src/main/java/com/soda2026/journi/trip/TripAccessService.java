package com.soda2026.journi.trip;

import com.soda2026.journi.tripMember.TripMemberId;
import com.soda2026.journi.tripMember.TripMemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class TripAccessService {

    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;

    public TripAccessService(TripRepository tripRepository,
                             TripMemberRepository tripMemberRepository) {
        this.tripRepository = tripRepository;
        this.tripMemberRepository = tripMemberRepository;
    }

    public boolean isOwner(Long tripId, Long userId) {
        requireAuthenticated(userId);
        return getTrip(tripId).getOwnerId().equals(userId);
    }

    public boolean isMember(Long tripId, Long userId) {
        requireAuthenticated(userId);
        getTrip(tripId);
        return tripMemberRepository.existsById(new TripMemberId(tripId, userId));
    }

    public void requireTripOwner(Long tripId, Long userId) {
        if (!isOwner(tripId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the trip owner may perform this action");
        }
    }

    public void requireTripOwnerOrMember(Long tripId, Long userId) {
        requireAuthenticated(userId);
        TripModel trip = getTrip(tripId);
        boolean isOwner = trip.getOwnerId().equals(userId);
        if (isOwner) {
            return;
        }

        boolean isMember = tripMemberRepository.existsById(new TripMemberId(tripId, userId));
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Trip access denied");
        }
    }

    private TripModel getTrip(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Trip not found"));
    }

    private void requireAuthenticated(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Authentication required");
        }
    }
}
