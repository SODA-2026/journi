package com.soda2026.journi.tripActivity;

import com.soda2026.journi.place.PlaceModel;
import com.soda2026.journi.place.PlaceRepository;
import com.soda2026.journi.trip.TripAccessService;
import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.trip.TripRepository;
import com.soda2026.journi.tripActivity.dto.TripActivityRequestDTO;
import com.soda2026.journi.tripActivity.dto.TripActivityResponseDTO;
import com.soda2026.journi.tripActivity.dto.UpdateActivityRequestDTO;
import com.soda2026.journi.tripDestination.TripDestinationModel;
import com.soda2026.journi.tripDestination.TripDestinationRepository;
import com.soda2026.journi.tripDestination.TripDestinationService;
import com.soda2026.journi.user.UserModel;
import com.soda2026.journi.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TripActivityServiceTest {

    @Mock private TripActivityRepository repository;
    @Mock private TripActivityMapper mapper;
    @Mock private TripDestinationRepository tripDestinationRepository;
    @Mock private PlaceRepository placeRepository;
    @Mock private TripRepository tripRepository;
    @Mock private TripDestinationService tripDestinationService;
    @Mock private UserRepository userRepository;
    @Mock private TripAccessService access;
    @Mock private PlaceModel place;

    @InjectMocks private TripActivityService service;

    private TripModel trip;
    private TripDestinationModel destination;
    private TripActivityRequestDTO request;
    private final UpdateActivityRequestDTO updateRequest = new UpdateActivityRequestDTO(
            "Ny aktivitet", LocalDate.of(2027, 6, 3), LocalTime.of(10, 30), "Beskrivning");

    @BeforeEach
    void setUp() {
        trip = mock(TripModel.class);
        when(trip.getStartDate()).thenReturn(LocalDate.of(2027, 5, 17));
        when(trip.getEndDate()).thenReturn(LocalDate.of(2027, 7, 17));
        when(trip.getId()).thenReturn(5L);

        destination = mock(TripDestinationModel.class);
        when(destination.getTrip()).thenReturn(trip);

        request = mock(TripActivityRequestDTO.class);
        when(request.tripDestinationId()).thenReturn(1L);
        when(request.placeId()).thenReturn(null);

        when(tripDestinationRepository.findByIdAndTrip_Id(1L, 1L))
                .thenReturn(Optional.of(destination));
    }

    private void givenDate(LocalDate date) {
        when(request.activityDate()).thenReturn(date);
    }

    private TripActivityModel existingActivity() {
        TripActivityModel activity = new TripActivityModel();
        activity.setId(100L);
        activity.setName("Gammal aktivitet");
        activity.setActivityDate(LocalDate.of(2027, 6, 2));
        activity.setStartTime(LocalTime.of(9, 0));
        activity.setDescription("Gammal beskrivning");
        activity.setTripDestination(destination);
        activity.setPlace(place);
        return activity;
    }

    // --- KAN-108: creation, creator and date validation ---

    @Test
    void createActivity_savesLoggedInUserAsCreator() {
        givenDate(LocalDate.of(2027, 6, 1));
        UserModel user = new UserModel();
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        TripActivityModel entity = new TripActivityModel();
        when(mapper.toEntity(request, destination, null)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);

        service.createActivity(1L, request, 5L);

        ArgumentCaptor<TripActivityModel> captor = ArgumentCaptor.forClass(TripActivityModel.class);
        verify(repository).save(captor.capture());
        assertSame(user, captor.getValue().getCreatedBy());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2027-05-16", "2027-07-18"})
    void createActivity_rejectsDateOutsideTrip(String date) {
        givenDate(LocalDate.parse(date));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.createActivity(1L, request, 5L));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getMessage().contains("Datumet ligger utanför resan"));
        verify(repository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2027-05-17", "2027-07-17"})
    void createActivity_allowsFirstAndLastDayOfTrip(String date) {
        givenDate(LocalDate.parse(date));
        when(userRepository.findById(5L)).thenReturn(Optional.of(new UserModel()));
        TripActivityModel entity = new TripActivityModel();
        when(mapper.toEntity(request, destination, null)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);

        assertDoesNotThrow(() -> service.createActivity(1L, request, 5L));
    }

    @Test
    void createActivity_allowsMissingDate() {
        givenDate(null);
        when(userRepository.findById(5L)).thenReturn(Optional.of(new UserModel()));
        TripActivityModel entity = new TripActivityModel();
        when(mapper.toEntity(request, destination, null)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);

        assertDoesNotThrow(() -> service.createActivity(1L, request, 5L));
    }

    // --- KAN-34: update authorization and preserved relations ---

    @Test
    void ownerUpdatesOnlyEditableFieldsAndKeepsPlaceAndDestination() {
        TripActivityModel activity = existingActivity();
        when(repository.findById(100L)).thenReturn(Optional.of(activity));
        when(repository.save(any())).thenAnswer(call -> call.getArgument(0));
        when(mapper.toDto(any())).thenReturn(new TripActivityResponseDTO(
                100L, 11L, "Ny aktivitet", LocalDate.of(2027, 6, 3),
                LocalTime.of(10, 30), "Beskrivning", TripActivityStatus.PLANNED, null, null));

        TripActivityResponseDTO response = service.updateActivity(100L, 1L, updateRequest);

        verify(access).requireTripOwnerOrMember(5L, 1L);
        verify(mapper).updateEntity(eq(updateRequest), same(activity));
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
                () -> service.updateActivity(100L, 3L, updateRequest));

        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verify(mapper, never()).updateEntity(any(UpdateActivityRequestDTO.class), any());
        verify(repository, never()).save(any());
    }

    @Test
    void updateActivity_rejectsDateOutsideTrip() {
        TripActivityModel activity = existingActivity();
        when(repository.findById(100L)).thenReturn(Optional.of(activity));
        UpdateActivityRequestDTO outsideTrip = new UpdateActivityRequestDTO(
                "Utanför resan", LocalDate.of(2027, 5, 16), LocalTime.of(10, 0), "Beskrivning");

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.updateActivity(100L, 1L, outsideTrip));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        assertTrue(error.getMessage().contains("Datumet ligger utanför resan"));
        verify(mapper, never()).updateEntity(any(UpdateActivityRequestDTO.class), any());
        verify(repository, never()).save(any());
    }

    @Test
    void missingActivityIsNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.updateActivity(999L, 1L, updateRequest));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verifyNoInteractions(access);
        verify(repository, never()).save(any());
    }

    // --- KAN-34: delete authorization ---

    @Test
    void ownerCanDeleteActivity() {
        TripActivityModel activity = existingActivity();
        when(repository.findById(100L)).thenReturn(Optional.of(activity));

        service.delete(100L, 1L);

        verify(access).requireTripOwnerOrMember(5L, 1L);
        verify(repository).deleteById(100L);
    }

    @Test
    void outsiderCannotDeleteActivity() {
        TripActivityModel activity = existingActivity();
        when(repository.findById(100L)).thenReturn(Optional.of(activity));
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Trip access denied"))
                .when(access).requireTripOwnerOrMember(5L, 3L);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.delete(100L, 3L));

        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void deletingMissingActivityIsNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.delete(999L, 1L));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verifyNoInteractions(access);
        verify(repository, never()).deleteById(anyLong());
    }
}
