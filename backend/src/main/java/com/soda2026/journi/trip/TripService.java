package com.soda2026.journi.trip;

import com.soda2026.journi.trip.dto.CreateTripRequest;
import com.soda2026.journi.trip.dto.TripResponse;
import com.soda2026.journi.tripDestination.TripDestinationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;
    private final TripDestinationService tripDestinationService;

    public TripService(TripRepository tripRepository, TripMapper tripMapper, TripDestinationService tripDestinationService) {
        this.tripRepository = tripRepository;
        this.tripMapper = tripMapper;
        this.tripDestinationService = tripDestinationService;
    }

    public List<TripResponse> getAll() {
        return tripRepository.findAll().stream()
                .map(tripMapper::toDto)
                .toList();
    }

    @Transactional
    public TripResponse createTrip(CreateTripRequest createTripRequest, Long ownerId){

        if(createTripRequest.getEndDate().isBefore(createTripRequest.getStartDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "End date must not be before start date"
            );
        }

        TripModel trip = tripMapper.toEntity(createTripRequest);
        trip.setOwnerId(ownerId);

        TripModel savedTrip = tripRepository.save(trip);

        tripDestinationService.createFirstDestination(
                savedTrip,
                createTripRequest.getCountryId(),
                createTripRequest.getCityId()
        );

        return tripMapper.toDto(savedTrip);

    }
}
