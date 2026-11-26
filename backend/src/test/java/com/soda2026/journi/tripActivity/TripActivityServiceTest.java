package com.soda2026.journi.tripActivity;

import com.soda2026.journi.place.PlaceModel;
import com.soda2026.journi.place.PlaceRepository;
import com.soda2026.journi.trip.TripAccessService;
import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.trip.TripRepository;
import com.soda2026.journi.tripActivity.dto.TripActivityResponseDTO;
import com.soda2026.journi.tripActivity.dto.UpdateActivityRequestDTO;
import com.soda2026.journi.tripDestination.TripDestinationModel;
import com.soda2026.journi.tripDestination.TripDestinationRepository;
import com.soda2026.journi.tripDestination.TripDestinationService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TripActivityServiceTest {
    private final TripActivityRepository repository = mock(TripActivityRepository.class);
    private final TripActivityMapper mapper = mock(TripActivityMapper.class);
    private final TripDestinationRepository destinations = mock(TripDestinationRepository.class);
    private final PlaceRepository places = mock(PlaceRepository.class);
    private final TripRepository trips = mock(TripRepository.class);
    private final TripDestinationService destinationService = mock(TripDestinationService.class);
    private final TripAccessService access = mock(TripAccessService.class);
    private final TripActivityService service = new TripActivityService(
            repository, mapper, destinations, places, trips, destinationService, access);

    private final TripDestinationModel destination = mock(TripDestinationModel.class);
    private final TripModel trip = mock(TripModel.class);
    private final PlaceModel place = mock(PlaceModel.class);

    private final UpdateActivityRequestDTO request = new UpdateActivityRequestDTO(
            "Ny aktivitet", LocalDate.of(2026, 7, 3), LocalTime.of(10, 30), "Beskrivning");

    private TripActivityModel existingActivity() {
        when(destination.getTrip()).thenReturn(trip);
        when(trip.getId()).thenReturn(5L);

        TripActivityModel activity = new TripActivityModel();
        activity.setId(100L);
        activity.setName("Gammal aktivitet");
        activity.setActivityDate(LocalDate.of(2026, 7, 2));
        activity.setStartTime(LocalTime.of(9, 0));
        activity.setDescription("Gammal beskrivning");
        activity.setTripDestination(destination);
        activity.setPlace(place);
        return activity;
    }

    @Test
    void ownerUpdatesOnlyEditableFieldsAndKeepsPlaceAndDestination() {
        TripActivityModel activity = existingActivity();
        when(repository.findById(100L)).thenReturn(Optional.of(activity));
        when(repository.save(any())).thenAnswer(call -> call.getArgument(0));
        when(mapper.toDto(any())).thenReturn(new TripActivityResponseDTO(
                100L, 11L, "Ny aktivitet", LocalDate.of(2026, 7, 3),
                LocalTime.of(10, 30), "Beskrivning", TripActivityStatus.PLANNED));

        TripActivityResponseDTO response = service.updateActivity(100L, 1L, request);

        verify(access).requireTripOwnerOrMember(5L, 1L);
        verify(mapper).updateEntity(eq(request), same(activity));
        verify(repository).save(activity);
        assertSame(place, activity.getPlace());
        assertSame(destination, activity.getTripDestination());
        assertEquals("Ny aktivitet", response.name());
    }

    @Test
    void outsiderCannotUpdate() {
        TripActivityModel activity = existingActivity();
        when(repository.findById(100L)).thenReturn(Optional.of(activity));
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Trip access denied"))
                .when(access).requireTripOwnerOrMember(5L, 3L);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.updateActivity(100L, 3L, request));

        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verify(mapper, never()).updateEntity(any(UpdateActivityRequestDTO.class), any());
        verify(repository, never()).save(any());
    }

    @Test
    void missingActivityIsNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.updateActivity(999L, 1L, request));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verifyNoInteractions(access);
        verify(repository, never()).save(any());
    }
}
