package com.soda2026.journi.tripDestination;

import com.soda2026.journi.city.CityModel;
import com.soda2026.journi.country.CountryModel;
import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.tripDestination.dto.CreateTripDestinationRequest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TripDestinationServiceTest {
    private final TripDestinationRepository repository = mock(TripDestinationRepository.class);
    private final EntityManager entityManager = mock(EntityManager.class);
    private final TripDestinationService service = new TripDestinationService(repository, entityManager, new TripDestinationMapper());

    @Test void rejectsCityFromAnotherCountry() {
        TripModel trip = trip();
        CountryModel selected = country(1L);
        CityModel city = city(5L, country(2L));
        when(entityManager.find(CountryModel.class, 1L)).thenReturn(selected);
        when(entityManager.find(CityModel.class, 5L)).thenReturn(city);
        assertError(HttpStatus.BAD_REQUEST, "Selected city does not belong to selected country",
                () -> service.createFirstDestination(trip, 1L, 5L));
        verifyNoInteractions(repository);
    }

    @Test void rejectsMissingCity() {
        when(entityManager.find(CountryModel.class, 1L)).thenReturn(country(1L));
        assertError(HttpStatus.NOT_FOUND, "City not found",
                () -> service.createFirstDestination(trip(), 1L, 999L));
        verifyNoInteractions(repository);
    }

    @Test void rejectsMissingCountry() {
        assertError(HttpStatus.NOT_FOUND, "Country not found",
                () -> service.createFirstDestination(trip(), 999L, 4L));
        verifyNoInteractions(repository);
        verify(entityManager, never()).find(eq(CityModel.class), any());
    }

    @Test void rejectsUnsavedTrip() {
        assertError(HttpStatus.BAD_REQUEST, "Trip must be saved before adding a destination",
                () -> service.createFirstDestination(new TripModel(), 1L, 4L));
        verifyNoInteractions(entityManager, repository);
    }

    @Test void createsFirstDestinationWithCityAndOrderZero() {
        TripModel trip = trip();
        CountryModel country = country(1L);
        CityModel city = city(4L, country);
        when(entityManager.find(CountryModel.class, 1L)).thenReturn(country);
        when(entityManager.find(CityModel.class, 4L)).thenReturn(city);
        when(repository.save(any())).thenAnswer(call -> call.getArgument(0));

        var response = service.createFirstDestination(trip, 1L, 4L);
        var captor = ArgumentCaptor.forClass(TripDestinationModel.class);
        verify(repository).save(captor.capture());
        assertSame(trip, captor.getValue().getTrip());
        assertSame(city, captor.getValue().getCity());
        assertEquals(0, captor.getValue().getOrderIndex());
        assertEquals(10L, response.tripId());
        assertEquals(4L, response.cityId());
        assertEquals(0, response.orderIndex());
    }

    @Test void rejectsOccupiedOrderIndex() {
        when(entityManager.find(TripModel.class, 10L)).thenReturn(trip());
        when(repository.existsByTrip_IdAndOrderIndex(10L, 2)).thenReturn(true);
        assertError(HttpStatus.CONFLICT, "Destination order index is already occupied",
                () -> service.createDestination(10L, new CreateTripDestinationRequest(4L, 2)));
        verify(repository, never()).save(any());
    }

    @Test void rejectsMissingTripWhenAddingDestination() {
        assertError(HttpStatus.NOT_FOUND, "Trip not found",
                () -> service.createDestination(10L, new CreateTripDestinationRequest(4L, 2)));
        verifyNoInteractions(repository);
    }

    @Test void rejectsMissingCityWhenAddingDestination() {
        when(entityManager.find(TripModel.class, 10L)).thenReturn(trip());
        assertError(HttpStatus.NOT_FOUND, "City not found",
                () -> service.createDestination(10L, new CreateTripDestinationRequest(999L, 2)));
        verify(repository, never()).save(any());
    }

    private void assertError(HttpStatus status, String reason, org.junit.jupiter.api.function.Executable action) {
        var error = assertThrows(ResponseStatusException.class, action);
        assertEquals(status, error.getStatusCode());
        assertEquals(reason, error.getReason());
    }
    private TripModel trip() {
        var trip = new TripModel(); trip.setId(10L); return trip;
    }
    private CountryModel country(Long id) {
        var country = new CountryModel(); country.setId(id); country.setName("Sverige"); return country;
    }
    private CityModel city(Long id, CountryModel country) {
        var city = new CityModel(); city.setId(id); city.setName("Malmö"); city.setCountry(country); return city;
    }
}
