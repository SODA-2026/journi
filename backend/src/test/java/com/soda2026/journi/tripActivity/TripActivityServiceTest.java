package com.soda2026.journi.tripActivity;

import com.soda2026.journi.place.PlaceRepository;
import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.trip.TripRepository;
import com.soda2026.journi.tripActivity.dto.TripActivityRequestDTO;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks private TripActivityService service;

    private TripDestinationModel destination;
    private TripActivityRequestDTO request;

    @BeforeEach
    void setUp() {
        TripModel trip = mock(TripModel.class);
        when(trip.getStartDate()).thenReturn(LocalDate.of(2027, 5, 17));
        when(trip.getEndDate()).thenReturn(LocalDate.of(2027, 7, 17));

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
}