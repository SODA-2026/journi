package com.soda2026.journi.trip;

 import com.soda2026.journi.trip.dto.*;
 import com.soda2026.journi.tripDestination.TripDestinationModel;
 import com.soda2026.journi.tripDestination.TripDestinationRepository;
 import com.soda2026.journi.tripDestination.TripDestinationService;
 import org.springframework.http.HttpStatus;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 import org.springframework.web.server.ResponseStatusException;

 import java.time.LocalDate;
 import java.util.List;

 @Service
 public class TripService {

     private final TripRepository tripRepository;
     private final TripDestinationService tripDestinationService;
     private final TripDestinationRepository tripDestinationRepository;
     private final TripMapper tripMapper;
     private final TripAccessService tripAccessService;


     public TripService(TripRepository tripRepository, TripMapper tripMapper, TripDestinationService tripDestinationService, TripDestinationRepository tripDestinationRepository, TripAccessService tripAccessService) {
         this.tripRepository = tripRepository;
         this.tripAccessService = tripAccessService;
         this.tripMapper = tripMapper;
         this.tripDestinationService = tripDestinationService;
         this.tripDestinationRepository = tripDestinationRepository;

     }

    public List<TripResponse> getAll() {
        return tripRepository.findAll().stream()
                .map(tripMapper::toDto)
                .toList();
    }

    @Transactional
    public TripResponse createTrip(CreateTripRequest createTripRequest, Long ownerId){
         validateDate(createTripRequest.getStartDate(), createTripRequest.getEndDate());
//        if(createTripRequest.getEndDate().isBefore(createTripRequest.getStartDate())) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "End date must not be before start date"
//            );
//        }

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

    @Transactional
    public void delete(Long id, Long currentUserId) {
        tripAccessService.requireTripOwner(id, currentUserId);
        tripRepository.deleteById(id);
    }

     @Transactional(readOnly = true)
     public List<TripSummaryDTO> findTripsByOwnerId(Long ownerId){
         return tripRepository.findAllByOwnerIdOrderByStartDateDesc(ownerId).stream()
                 .map(tripMapper::toTripSummaryDTO)
                 .toList();
     }

     @Transactional(readOnly = true)
     public TripInfoDTO getTripInfo(Long tripId, Long userId){
         tripAccessService.requireTripOwnerOrMember(tripId, userId);
         TripModel trip = tripRepository.findById(tripId)
                 .orElseThrow(() -> new ResponseStatusException(
                         HttpStatus.NOT_FOUND,
                         "Hittade ingen resa" ));

         List<TripDestinationModel> destinations = tripDestinationRepository.findAllByTrip_IdOrderByOrderIndexAsc(tripId);
         return tripMapper.toTripInfoDTO(trip, destinations);
     }

     @Transactional
     public UpdateTripResponseDTO updateTrip(Long tripId, Long userId, UpdateTripRequestDTO dto){
         tripAccessService.requireTripOwner(tripId, userId);

         TripModel trip = tripRepository.findById(tripId)
                 .orElseThrow(()-> new ResponseStatusException(
                         HttpStatus.NOT_FOUND,
                         "Resan hittades inte"
                 ));

         validateDate(dto.startDate(), dto.endDate());
         if (!dto.startDate().equals(trip.getStartDate()) && dto.startDate().isBefore(LocalDate.now())){
             throw new ResponseStatusException(
                     HttpStatus.BAD_REQUEST,
                     "startdatumet du försöker sätta har redan varit"
             );
         }

         trip.setTitle(dto.title());
         trip.setStartDate(dto.startDate());
         trip.setEndDate(dto.endDate());

         return tripMapper.toUpdateTripResponseDTO(tripRepository.save(trip));

     }

     //Metod för att kolla om startdatum är före slutdatum
     private void validateDate(LocalDate startDate, LocalDate endDate){
         if (endDate.isBefore(startDate)){
             throw new ResponseStatusException(
                     HttpStatus.BAD_REQUEST,
                     "End date must not be before start date"
             );
         }
     }
 }
