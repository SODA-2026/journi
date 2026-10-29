package com.soda2026.journi.trip;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.soda2026.journi.user.UserRepository;
import com.soda2026.journi.tripDestination.TripDestinationRepository;
import com.soda2026.journi.tripDestination.TripDestinationModel;
import java.security.Principal;
import java.time.Instant;

import java.util.List;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;
    private final UserRepository userRepository;
    private final TripDestinationRepository destinationRepository;

    public TripService(TripRepository tripRepository, TripMapper tripMapper,
                       UserRepository userRepository, TripDestinationRepository destinationRepository) {
        this.tripRepository = tripRepository;
        this.tripMapper = tripMapper;
        this.userRepository = userRepository;
        this.destinationRepository = destinationRepository;
    }

    @Transactional
    public CreatedTripResponse create(CreateTripRequest request, Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Logga in för att skapa en resa.");
        }
        var owner = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Användaren kunde inte hittas."));
        if (request.endDate().isBefore(request.startDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Slutdatum får inte vara före startdatum.");
        }
        var trip = new TripModel();
        trip.setOwnerId(owner.getId());
        trip.setTitle(request.city());
        trip.setStartDate(request.startDate());
        trip.setEndDate(request.endDate());
        var now = Instant.now();
        trip.setCreatedAt(now);
        trip.setUpdatedAt(now);
        trip = tripRepository.saveAndFlush(trip);
        destinationRepository.saveAndFlush(new TripDestinationModel(
                trip, null, request.country(), request.city(), 0));
        return new CreatedTripResponse(trip.getId(), request.country(), request.city(),
                trip.getStartDate(), trip.getEndDate());
    }

    public List<TripResponseDTO> getAll() {
        return tripRepository.findAll().stream()
                .map(tripMapper::toDto)
                .toList();
    }
}
