 package com.soda2026.journi.trip;

 import com.soda2026.journi.trip.dto.CreateTripRequest;
 import com.soda2026.journi.trip.dto.TripInfoDTO;
 import com.soda2026.journi.trip.dto.TripResponse;
 import com.soda2026.journi.trip.dto.TripSummaryDTO;
 import com.soda2026.journi.tripDestination.TripDestinationMapper;
 import com.soda2026.journi.tripDestination.TripDestinationModel;
 import com.soda2026.journi.tripDestination.TripDestinationRepository;
 import com.soda2026.journi.tripDestination.TripDestinationService;
 import com.soda2026.journi.tripDestination.dto.TripDestinationSummaryDTO;
 import org.springframework.http.HttpStatus;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 import org.springframework.web.server.ResponseStatusException;

 import java.util.List;

 @Service
 public class TripService {

     private final TripRepository tripRepository;
     private final TripDestinationService tripDestinationService;
     private final TripDestinationRepository tripDestinationRepository;
     private final TripMapper tripMapper;
     private final TripDestinationMapper tripDestinationMapper;

     public TripService(TripRepository tripRepository, TripMapper tripMapper, TripDestinationService tripDestinationService, TripDestinationRepository tripDestinationRepository, TripDestinationMapper tripDestinationMapper) {
         this.tripRepository = tripRepository;
         this.tripMapper = tripMapper;
         this.tripDestinationService = tripDestinationService;
         this.tripDestinationRepository = tripDestinationRepository;
         this.tripDestinationMapper = tripDestinationMapper;
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

     @Transactional(readOnly = true)
     public List<TripSummaryDTO> findTripsByOwnerId(Long ownerId){
         return tripRepository.findAllByOwnerIdOrderByStartDateDesc(ownerId).stream()
                 .map(tripMapper::toTripSummaryDTO)
                 .toList();
     }

     @Transactional(readOnly = true)
     public TripInfoDTO getTripInfo(Long tripId, Long userId){
         TripModel trip = tripRepository.findById(tripId)
                 .orElseThrow(() -> new ResponseStatusException(
                         HttpStatus.NOT_FOUND,
                         "Hittade ingen resa" ));

         if (!trip.getOwnerId().equals(userId)){
             throw new ResponseStatusException(
                     HttpStatus.FORBIDDEN,
                     "Du har inte tillgång till denna resa"
             );
         }

         List<TripDestinationModel> destinations = tripDestinationRepository.findAllByTrip_IdOrderByOrderIndexAsc(tripId);
         return tripMapper.toTripInfoDTO(trip, destinations);
     }
 }
