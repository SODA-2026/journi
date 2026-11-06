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
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 import org.springframework.validation.annotation.Validated;

 import java.util.List;
 import java.util.Objects;

 @Service
 @Validated
 public class TripDestinationService {
     private final TripDestinationRepository repository;
     private final EntityManager entityManager;
     private final TripDestinationMapper mapper;

     public TripDestinationService(
             TripDestinationRepository repository,
             EntityManager entityManager,
             TripDestinationMapper mapper
     ) {
         this.repository = repository;
         this.entityManager = entityManager;
         this.mapper = mapper;
     }

     @Transactional(readOnly = true)
     public List<TripDestinationResponse> getDestinations(Long tripId) {
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

         TripModel trip = entityManager.find(TripModel.class, tripId);
         if (trip == null) {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found");
         }

         requireCanEdit(trip, user);
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

     private void requireCanEdit(TripModel trip, UserModel user) {
         if (!Objects.equals(trip.getOwnerId(), user.getId())) {
             throw new ResponseStatusException(
                     HttpStatus.FORBIDDEN,
                     "Only the trip owner may add destinations"
             );
         }
     }
 }