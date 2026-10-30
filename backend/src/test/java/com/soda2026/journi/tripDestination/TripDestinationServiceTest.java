package com.soda2026.journi.tripDestination;

import com.soda2026.journi.tripDestination.dto.CreateTripDestinationRequest;
import com.soda2026.journi.suggestion.DestinationKind;
import com.soda2026.journi.suggestion.SuggestionModel;
import com.soda2026.journi.suggestion.SuggestionType;
import com.soda2026.journi.trip.TripModel;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TripDestinationServiceTest {
    @Test
    void rejectsCityFromAnotherCountry() {
        TripDestinationRepository repository =
                mock(TripDestinationRepository.class);
        EntityManager entityManager = mock(EntityManager.class);

        TripDestinationService service =
                new TripDestinationService(repository, entityManager, new TripDestinationMapper());

        TripModel trip = mock(TripModel.class);
        when(trip.getId()).thenReturn(10L);

        SuggestionModel selectedCountry = new SuggestionModel();
        selectedCountry.setId(1L);
        selectedCountry.setType(SuggestionType.DESTINATION);
        selectedCountry.setDestinationKind(DestinationKind.COUNTRY);

        SuggestionModel anotherCountry = new SuggestionModel();
        anotherCountry.setId(2L);

        SuggestionModel city = new SuggestionModel();
        city.setId(5L);
        city.setType(SuggestionType.DESTINATION);
        city.setDestinationKind(DestinationKind.CITY);
        city.setParentSuggestion(anotherCountry);

        when(entityManager.find(SuggestionModel.class, 1L))
                .thenReturn(selectedCountry);
        when(entityManager.find(SuggestionModel.class, 5L))
                .thenReturn(city);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.createFirstDestination(trip, 1L, 5L)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(
                "Selected city does not belong to selected country",
                exception.getReason()
        );
        verify(repository, never()).save(any(TripDestinationModel.class));
    }

    @Test
    void rejectsMissingCity() {
        TripDestinationRepository repository =
                mock(TripDestinationRepository.class);
        EntityManager entityManager = mock(EntityManager.class);

        TripDestinationService service =
                new TripDestinationService(repository, entityManager, new TripDestinationMapper());

        TripModel trip = mock(TripModel.class);
        when(trip.getId()).thenReturn(10L);

        SuggestionModel country = new SuggestionModel();
        country.setId(1L);
        country.setType(SuggestionType.DESTINATION);
        country.setDestinationKind(DestinationKind.COUNTRY);

        when(entityManager.find(SuggestionModel.class, 1L))
                .thenReturn(country);
        when(entityManager.find(SuggestionModel.class, 999L))
                .thenReturn(null);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.createFirstDestination(trip, 1L, 999L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("City not found", exception.getReason());

        verify(repository, never()).save(any(TripDestinationModel.class));
    }

    @Test
    void createsFirstDestination() {
        TripDestinationRepository repository =
                mock(TripDestinationRepository.class);
        EntityManager entityManager = mock(EntityManager.class);

        TripDestinationService service =
                new TripDestinationService(repository, entityManager, new TripDestinationMapper());

        TripModel trip = mock(TripModel.class);
        when(trip.getId()).thenReturn(10L);
        when(entityManager.find(TripModel.class, 10L))
                .thenReturn(trip);

        SuggestionModel country = new SuggestionModel();
        country.setId(1L);
        country.setType(SuggestionType.DESTINATION);
        country.setDestinationKind(DestinationKind.COUNTRY);
        country.setCountry("Sverige");

        SuggestionModel city = new SuggestionModel();
        city.setId(4L);
        city.setType(SuggestionType.DESTINATION);
        city.setDestinationKind(DestinationKind.CITY);
        city.setParentSuggestion(country);
        city.setCountry("Sverige");
        city.setName("Malmö");

        when(entityManager.find(SuggestionModel.class, 1L))
                .thenReturn(country);
        when(entityManager.find(SuggestionModel.class, 4L))
                .thenReturn(city);

        when(repository.existsByTrip_IdAndOrderIndex(10L, 0))
                .thenReturn(false);
        when(repository.save(any(TripDestinationModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.createFirstDestination(trip, 1L, 4L);

        ArgumentCaptor<TripDestinationModel> captor =
                ArgumentCaptor.forClass(TripDestinationModel.class);
        verify(repository).save(captor.capture());

        TripDestinationModel savedDestination = captor.getValue();

        assertEquals(trip, savedDestination.getTrip());
        assertEquals(city, savedDestination.getSourceSuggestion());
        assertEquals("Sverige", savedDestination.getCountry());
        assertEquals("Malmö", savedDestination.getPlaceName());
        assertEquals(Integer.valueOf(0), savedDestination.getOrderIndex());

        assertEquals(Long.valueOf(10L), response.tripId());
        assertEquals(Long.valueOf(4L), response.sourceSuggestionId());
        assertEquals("Sverige", response.country());
        assertEquals("Malmö", response.placeName());
        assertEquals(Integer.valueOf(0), response.orderIndex());

    }

    @Test
    void rejectsOccupiedOrderIndex() {
        TripDestinationRepository repository =
                mock(TripDestinationRepository.class);
        EntityManager entityManager = mock(EntityManager.class);

        TripDestinationService service =
                new TripDestinationService(repository, entityManager, new TripDestinationMapper());

        TripModel trip = mock(TripModel.class);
        when(entityManager.find(TripModel.class, 10L))
                .thenReturn(trip);

        when(repository.existsByTrip_IdAndOrderIndex(10L, 0))
                .thenReturn(true);

        CreateTripDestinationRequest request =
                new CreateTripDestinationRequest(4L, 0);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.createDestination(10L, request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals(
                "Destination order index is already occupied",
                exception.getReason()
        );

        verify(repository, never()).save(any(TripDestinationModel.class));
    }
}
