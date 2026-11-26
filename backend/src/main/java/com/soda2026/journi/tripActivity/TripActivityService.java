 package com.soda2026.journi.tripActivity;

 import com.soda2026.journi.place.PlaceModel;
 import com.soda2026.journi.place.PlaceRepository;
 import com.soda2026.journi.trip.TripModel;
 import com.soda2026.journi.trip.TripRepository;
   import com.soda2026.journi.trip.TripAccessService;
 import com.soda2026.journi.tripActivity.TripActivityMapper;
 import com.soda2026.journi.tripActivity.TripActivityRepository;
 import com.soda2026.journi.tripActivity.dto.AddPlaceActivityDTO;
 import com.soda2026.journi.tripActivity.dto.TripActivityRequestDTO;
  import com.soda2026.journi.tripActivity.dto.UpdateActivityRequestDTO;
 import com.soda2026.journi.tripActivity.dto.TripActivityResponseDTO;
 import com.soda2026.journi.tripDestination.TripDestinationModel;
 import com.soda2026.journi.tripDestination.TripDestinationRepository;
 import com.soda2026.journi.tripDestination.TripDestinationService;
 import com.soda2026.journi.user.UserModel;
 import com.soda2026.journi.user.UserRepository;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.HttpStatusCode;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 import org.springframework.web.server.ResponseStatusException;

 import java.time.LocalDate;
 import java.util.List;

 @Service
 public class TripActivityService {
     private final TripActivityRepository repository;
     private final TripActivityMapper mapper;
     private final TripDestinationRepository tripDestinationRepository;
     private final PlaceRepository placeRepository;
     private final TripRepository tripRepository;
     private final TripDestinationService tripDestinationService;
     private final UserRepository userRepository;
     private final TripAccessService tripAccessService;

     public TripActivityService(TripActivityRepository repository, TripActivityMapper mapper, TripDestinationRepository tripDestinationRepository, PlaceRepository placeRepository, TripRepository tripRepository, TripDestinationService tripDestinationService, UserRepository userRepository, TripAccessService tripAccessService) {
         this.repository = repository;
         this.mapper = mapper;
         this.tripDestinationRepository = tripDestinationRepository;
         this.placeRepository = placeRepository;
         this.tripRepository = tripRepository;
         this.tripDestinationService = tripDestinationService;
         this.userRepository = userRepository;
         this.tripAccessService = tripAccessService;
     }


     public List<TripActivityResponseDTO> findAll() {
         return mapper.toDtos(repository.findAll());
     }

     @Transactional(readOnly = true)
     public TripActivityResponseDTO findById(Long id) {
         return mapper.toDto(repository.findById(id)
                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found")));
         //      .orElseThrow(() -> new ResourceNotFoundExeption("Aktiviteten hittades inte.")));

     }


     @Transactional
     public TripActivityResponseDTO createActivity(Long tripId, TripActivityRequestDTO request, Long userId){
         TripDestinationModel destination = tripDestinationRepository
                 .findByIdAndTrip_Id(request.tripDestinationId(), tripId)
                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination not found for this trip" ));
//                 .orElseThrow(() -> new ResourceNotFoundExeption("Resmålet hittades inte för den här resan"));

         validateActivityDate(destination.getTrip(), request.activityDate());

         PlaceModel place = null;
         if (request.placeId() != null) {
             place = placeRepository.findById(request.placeId())
                     .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Place not found"));
         }

         TripActivityModel activity = mapper.toEntity(request, destination, place);
         activity.setCreatedBy(getCreator(userId));

         return mapper.toDto(repository.save(activity));
     }

     private UserModel getCreator(Long userId){
         return  userRepository.findById(userId)
                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
     }

     private void validateActivityDate(TripModel trip, LocalDate date) {
         if (date == null) {
             return; // activity_date is optional in the database
         }

         LocalDate start = trip.getStartDate();
         LocalDate end = trip.getEndDate();

         if (date.isBefore(start) || date.isAfter(end)) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                     "Datumet måste ligga mellan " + start + " och " + end);
         }
     }

     @Transactional
     public TripActivityResponseDTO updateActivity(Long activityId, Long userId, UpdateActivityRequestDTO dto) {
         TripActivityModel activity = repository.findById(activityId)
                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found"));

         Long tripId = activity.getTripDestination().getTrip().getId();
         tripAccessService.requireTripOwnerOrMember(tripId, userId);

         mapper.updateEntity(dto, activity);
         return mapper.toDto(repository.save(activity));
     }

     @Transactional
     public  List<TripActivityResponseDTO> getActivityForDestination(Long tripId, Long tripDestinationId){
         if(!tripDestinationRepository.existsByIdAndTrip_Id(tripDestinationId, tripId)){
//             throw new ResourceNotFoundException("Resmålet hittades inte för den här resan");
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination not found for this trip");
         }
         return mapper.toDtos(
                 repository.findByTripDestinationIdOrderByActivityDateAscStartTimeAsc(tripDestinationId));
     }

     public void delete(Long id) {
         repository.deleteById(id);
     }

    @Transactional
     public void addPlaceToActivity(Long tripId, AddPlaceActivityDTO request, Long userId) {
        PlaceModel place = placeRepository.findById(request.placeId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Place not found"
                        ));
        Long cityId = place.getCity().getId();

        TripDestinationModel destination =
                tripDestinationRepository
                        .findByTrip_IdAndCity_Id(
                                tripId,
                                cityId
                        )
                        .orElse(null);

        if(destination == null) {
            TripModel trip = tripRepository.findById(tripId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Trip not found"
                        ));

            int orderIndex =
                    tripDestinationRepository
                            .findAllByTrip_IdOrderByOrderIndexAsc(tripId)
                            .size();

            tripDestinationService.createDestination(
                    trip,
                    cityId,
                    orderIndex
            );

            destination = tripDestinationRepository
                    .findByTrip_IdAndCity_Id(
                            tripId,
                            cityId
                    )
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.INTERNAL_SERVER_ERROR,
                                    "Destination creation failed"
                            ));
        }

        TripActivityModel activity = new TripActivityModel();

        activity.setPlace(place);
        activity.setTripDestination(destination);
        activity.setName(place.getName());
        activity.setCreatedBy(getCreator(userId));

        repository.save(activity);
    }



 }
