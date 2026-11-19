 package com.soda2026.journi.tripActivity;

 import com.soda2026.journi.tripActivity.TripActivityMapper;
 import com.soda2026.journi.tripActivity.TripActivityRepository;
 import com.soda2026.journi.tripActivity.dto.TripActivityRequestDTO;
 import com.soda2026.journi.tripActivity.dto.TripActivityResponseDTO;
 import com.soda2026.journi.tripDestination.TripDestinationModel;
 import com.soda2026.journi.tripDestination.TripDestinationRepository;
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

     public TripActivityService(TripActivityRepository repository, TripActivityMapper mapper, TripDestinationRepository tripDestinationRepository) {
         this.repository = repository;
         this.mapper = mapper;
         this.tripDestinationRepository = tripDestinationRepository;
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
         TripActivityModel saved = repository.save(mapper.toEntity(request, destination));
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



 }
