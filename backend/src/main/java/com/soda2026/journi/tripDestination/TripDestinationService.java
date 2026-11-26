 package com.soda2026.journi.tripDestination;

 import com.soda2026.journi.city.CityModel;
 import com.soda2026.journi.country.CountryModel;
 import com.soda2026.journi.tripDestination.dto.CreateTripDestinationRequest;
 import com.soda2026.journi.tripDestination.dto.TripDestinationResponse;
 import com.soda2026.journi.trip.TripModel;
 import com.soda2026.journi.user.UserModel;
 import jakarta.persistence.EntityManager;
 import jakarta.validation.Valid;
 import org.springframework.http.HttpStatus;
 import org.springframework.web.server.ResponseStatusException;
 import jakarta.validation.constraints.NotNull;
 import jakarta.validation.constraints.Positive;
 import jakarta.validation.constraints.PositiveOrZero;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 import org.springframework.validation.annotation.Validated;

 import java.util.List;
 import java.util.HashSet;
 import java.util.ArrayList;
 import com.soda2026.journi.trip.TripAccessService;
 import com.soda2026.journi.tripActivity.TripActivityRepository;

 @Service
 @Validated
 public class TripDestinationService {
     private final TripDestinationRepository repository;
     private final EntityManager entityManager;
     private final TripDestinationMapper mapper;
     private final TripAccessService tripAccessService;
     private final TripActivityRepository tripActivityRepository;

     public TripDestinationService(
             TripDestinationRepository repository,
             EntityManager entityManager,
             TripDestinationMapper mapper,
             TripAccessService tripAccessService,
             TripActivityRepository tripActivityRepository
     ) {
         this.repository = repository;
         this.entityManager = entityManager;
         this.mapper = mapper;
         this.tripAccessService = tripAccessService;
         this.tripActivityRepository = tripActivityRepository;
     }

     @Transactional(readOnly = true)
     public List<TripDestinationResponse> getDestinations(Long tripId, Long userId) {
         tripAccessService.requireTripOwnerOrMember(tripId, userId);
         return repository.findAllByTrip_IdOrderByOrderIndexAsc(tripId)
                 .stream()
                 .map(mapper::toDto)
                 .toList();
     }

     @Transactional
     public TripDestinationResponse createDestination(
             @NotNull @Positive Long tripId,
             @NotNull @Valid CreateTripDestinationRequest request,
             @NotNull @Positive Long userId
     ) {
         UserModel user = entityManager.find(UserModel.class, userId);
         if (user == null) {
             throw new ResponseStatusException(
                     HttpStatus.NOT_FOUND, "User not found"
             );
         }

         tripAccessService.requireTripOwnerOrMember(tripId, userId);
         return createDestination(tripId, request);
     }

     @Transactional
     TripDestinationResponse createDestination(
             @NotNull @Positive Long tripId,
             @NotNull @Valid CreateTripDestinationRequest request
     ) {
         TripModel trip = entityManager.find(TripModel.class, tripId);
         if (trip == null) {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found");
         }
         if (repository.existsByTrip_IdAndOrderIndex(tripId, request.orderIndex())) {
             throw new ResponseStatusException(
                     HttpStatus.CONFLICT,
                     "Destination order index is already occupied"
             );
         }
         CityModel city = entityManager.find(
                 CityModel.class, request.cityId()
         );
         if (city == null) {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found");
         }

         TripDestinationModel destination = new TripDestinationModel(
                 trip,
                 city,
                 request.orderIndex()
         );

         TripDestinationModel savedDestination = repository.save(destination);
         return mapper.toDto(savedDestination);
     }

     @Transactional
     public TripDestinationResponse createDestination(
             @NotNull TripModel trip,
             @NotNull @Positive Long cityId,
             @NotNull @PositiveOrZero Integer orderIndex
     ) {
         CityModel city = entityManager.find(CityModel.class, cityId);
         if (city == null) {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found");
         }
         if (repository.existsByTrip_IdAndOrderIndex(trip.getId(), orderIndex)) {
             throw new ResponseStatusException(
                     HttpStatus.CONFLICT,
                     "Destination order index is already occupied"
             );
         }
         TripDestinationModel destination = new TripDestinationModel(trip, city, orderIndex);
         TripDestinationModel saved = repository.save(destination);
         return mapper.toDto(saved);
     }

     @Transactional
     public List<TripDestinationResponse> reorderDestinations(
             @NotNull @Positive Long tripId,
             @NotNull List<@Positive Long> destinationIds,
             @NotNull @Positive Long userId
     ) {
         tripAccessService.requireTripOwnerOrMember(tripId, userId);
         List<TripDestinationModel> destinations =
                 repository.findAllByTrip_IdOrderByOrderIndexAsc(tripId);

         if (destinationIds.size() != destinations.size()
                 || new HashSet<>(destinationIds).size() != destinationIds.size()) {
             throw new ResponseStatusException(
                     HttpStatus.BAD_REQUEST,
                     "Reorder must contain each destination exactly once"
             );
         }

         java.util.Map<Long, TripDestinationModel> byId = new java.util.HashMap<>();
         for (TripDestinationModel destination : destinations) {
             byId.put(destination.getId(), destination);
         }
         if (!byId.keySet().containsAll(destinationIds)) {
             throw new ResponseStatusException(
                     HttpStatus.BAD_REQUEST,
                     "Reorder contains a destination that does not belong to the trip"
             );
         }

         for (int i = 0; i < destinations.size(); i++) {
             byId.get(destinationIds.get(i)).setOrderIndex(Integer.MAX_VALUE - i);
         }
         repository.saveAll(destinations);
         repository.flush();

         List<TripDestinationModel> reordered = new ArrayList<>();
         for (int i = 0; i < destinationIds.size(); i++) {
             TripDestinationModel destination = byId.get(destinationIds.get(i));
             destination.setOrderIndex(i);
             reordered.add(destination);
         }
         repository.saveAll(reordered);

         return reordered.stream().map(mapper::toDto).toList();
     }

     @Transactional
     public void deleteDestination(
             @NotNull @Positive Long tripId,
             @NotNull @Positive Long destinationId,
             @NotNull @Positive Long userId
     ) {
         tripAccessService.requireTripOwnerOrMember(tripId, userId);
         TripDestinationModel destination = repository
                 .findByIdAndTrip_Id(destinationId, tripId)
                 .orElseThrow(() -> new ResponseStatusException(
                         HttpStatus.NOT_FOUND, "Destination not found"
                 ));

         tripActivityRepository.deleteAllByTripDestination_Id(destinationId);
         repository.delete(destination);
         repository.flush();

         List<TripDestinationModel> remaining =
                 repository.findAllByTrip_IdOrderByOrderIndexAsc(tripId);
         for (int i = 0; i < remaining.size(); i++) {
             remaining.get(i).setOrderIndex(i);
         }
         repository.saveAll(remaining);
     }

     @Transactional
     public TripDestinationResponse createFirstDestination(
             @NotNull TripModel trip,
             @NotNull @Positive Long countryId,
             @NotNull @Positive Long cityId
     ) {
         if (trip.getId() == null) {
             throw new ResponseStatusException(
                     HttpStatus.BAD_REQUEST,
                     "Trip must be saved before adding a destination"
             );
         }
         CountryModel country = entityManager.find(
                 CountryModel.class, countryId
         );
         if (country == null) {
             throw new ResponseStatusException(
                     HttpStatus.NOT_FOUND, "Country not found"
             );
         }

         CityModel city = entityManager.find(
                 CityModel.class, cityId
         );
         if (city == null) {
             throw new ResponseStatusException(
                     HttpStatus.NOT_FOUND, "City not found"
             );
         }

         if (!countryId.equals(city.getCountry().getId())) {
             throw new ResponseStatusException(
                     HttpStatus.BAD_REQUEST,
                     "Selected city does not belong to selected country"
             );
         }

        TripDestinationModel destination =
                new TripDestinationModel(trip, city, 0);
         TripDestinationModel saved = repository.save(destination);

         return mapper.toDto(saved);
     }

 }
