 package com.soda2026.journi.tripActivity;

 import com.soda2026.journi.place.PlaceModel;
 import com.soda2026.journi.place.PlaceRepository;
 import com.soda2026.journi.trip.TripModel;
 import com.soda2026.journi.trip.TripRepository;
 import com.soda2026.journi.tripActivity.TripActivityMapper;
 import com.soda2026.journi.tripActivity.TripActivityRepository;
 import com.soda2026.journi.tripActivity.dto.AddPlaceActivityDTO;
 import com.soda2026.journi.tripActivity.dto.TripActivityRequestDTO;
 import com.soda2026.journi.tripActivity.dto.TripActivityResponseDTO;
 import com.soda2026.journi.tripDestination.TripDestinationModel;
 import com.soda2026.journi.tripDestination.TripDestinationRepository;
 import com.soda2026.journi.tripDestination.TripDestinationService;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.HttpStatusCode;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 import org.springframework.web.server.ResponseStatusException;

 import java.util.List;

 @Service
 public class TripActivityService {
     private final TripActivityRepository repository;
     private final TripActivityMapper mapper;
     private final TripDestinationRepository tripDestinationRepository;
     private final PlaceRepository placeRepository;
     private final TripRepository tripRepository;
     private final TripDestinationService tripDestinationService;

     public TripActivityService(TripActivityRepository repository, TripActivityMapper mapper, TripDestinationRepository tripDestinationRepository, PlaceRepository placeRepository, TripRepository tripRepository, TripDestinationService tripDestinationService) {
         this.repository = repository;
         this.mapper = mapper;
         this.tripDestinationRepository = tripDestinationRepository;
         this.placeRepository = placeRepository;
         this.tripRepository = tripRepository;
         this.tripDestinationService = tripDestinationService;
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
     public TripActivityResponseDTO createActivity(Long tripId, TripActivityRequestDTO request){
         TripDestinationModel destination = tripDestinationRepository
                 .findByIdAndTrip_Id(request.tripDestinationId(), tripId)
                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination not found for this trip" ));
//                 .orElseThrow(() -> new ResourceNotFoundExeption("Resmålet hittades inte för den här resan"));
         PlaceModel place =
                 placeRepository.findById(request.placeId())
                         .orElseThrow(() ->
                                 new ResponseStatusException(
                                         HttpStatus.NOT_FOUND,
                                         "Place not found"
                                 ));
         TripActivityModel saved = repository.save(mapper.toEntity(request, destination, place));
         return mapper.toDto(saved);
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
     public void addPlaceToActivity(Long tripId, AddPlaceActivityDTO request) {
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

        repository.save(activity);
    }



 }
