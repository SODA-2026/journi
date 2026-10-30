package com.soda2026.journi.tripDestination;

import jakarta.persistence.EntityManager;
import com.soda2026.journi.suggestion.DestinationKind;
import com.soda2026.journi.suggestion.SuggestionModel;
import com.soda2026.journi.suggestion.SuggestionType;
import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.tripMember.TripMemberId;
import com.soda2026.journi.tripMember.TripMemberModel;
import com.soda2026.journi.tripMember.TripMemberRole;
import com.soda2026.journi.user.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TripDestinationControllerTest {

    private TripDestinationRepository repository;
    private EntityManager entityManager;
    private MockMvc mockMvc;
    private TripDestinationService service;

    @BeforeEach
    void setUp() {
        repository = mock(TripDestinationRepository.class);
        entityManager = mock(EntityManager.class);

        service = new TripDestinationService(
                repository,
                entityManager,
                new TripDestinationMapper()
        );

        TripDestinationController controller =
                new TripDestinationController(service);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void rejectsInvalidRequestBeforeSaving() throws Exception {
        mockMvc.perform(post("/api/trips/10/destinations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sourceSuggestionId": null,
                                  "orderIndex": -1
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(entityManager, repository);
    }
    @Test
    void createsDestinationForOwner() throws Exception {
        TripModel trip = trip();
        stubUser(1L);
        when(entityManager.find(TripModel.class, 10L)).thenReturn(trip);
        SuggestionModel city = city();
        when(entityManager.find(SuggestionModel.class, 4L)).thenReturn(city);
        when(repository.save(any(TripDestinationModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/trips/10/destinations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sourceSuggestionId": 4, "orderIndex": 0}
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tripId").value(10))
                .andExpect(jsonPath("$.sourceSuggestionId").value(4))
                .andExpect(jsonPath("$.country").value("Sverige"))
                .andExpect(jsonPath("$.placeName").value("Malmö"))
                .andExpect(jsonPath("$.orderIndex").value(0));

        verify(repository).save(argThat(destination ->
                destination.getTrip() == trip
                        && destination.getSourceSuggestion() == city
                        && "Sverige".equals(destination.getCountry())
                        && "Malmö".equals(destination.getPlaceName())
                        && Integer.valueOf(0).equals(destination.getOrderIndex())));
    }

    @Test
    void returnsDestinationList() throws Exception {
        TripModel trip = trip();
        SuggestionModel city = city();
        when(repository.findAllByTrip_IdOrderByOrderIndexAsc(10L))
                .thenReturn(List.of(
                        new TripDestinationModel(trip, city, "Sverige", "Malmö", 0),
                        new TripDestinationModel(trip, null, "Norge", "Oslo", 1)
                ));

        mockMvc.perform(get("/api/trips/10/destinations"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        [
                          {"id": null, "tripId": 10, "sourceSuggestionId": 4,
                           "country": "Sverige", "placeName": "Malmö", "orderIndex": 0},
                          {"id": null, "tripId": 10, "sourceSuggestionId": null,
                           "country": "Norge", "placeName": "Oslo", "orderIndex": 1}
                        ]
                        """))
                .andExpect(jsonPath("$[0].orderIndex").value(0))
                .andExpect(jsonPath("$[1].orderIndex").value(1));

        verify(repository).findAllByTrip_IdOrderByOrderIndexAsc(10L);
        verify(repository, never()).save(any(TripDestinationModel.class));
    }

    @Test
    void rejectsPostByViewer() throws Exception {
        TripModel trip = trip();
        trip.setOwnerId(7L);
        UserModel viewer = stubUser(1L);
        when(entityManager.find(TripModel.class, 10L)).thenReturn(trip);
        when(entityManager.find(TripMemberModel.class, new TripMemberId(10L, 1L)))
                .thenReturn(new TripMemberModel(trip, viewer, TripMemberRole.VIEWER));

        mockMvc.perform(post("/api/trips/10/destinations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sourceSuggestionId": 4, "orderIndex": 0}
                                """))
                .andExpect(status().isForbidden());

        verifyNoInteractions(repository);
    }

    @Test
    void rejectsMissingTemporaryUser() throws Exception {
        mockMvc.perform(post("/api/trips/10/destinations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sourceSuggestionId\":4,\"orderIndex\":0}"))
                .andExpect(status().isNotFound());
        verifyNoInteractions(repository);
    }

    private UserModel stubUser(Long id) {
        UserModel user = new UserModel();
        user.setId(id);
        when(entityManager.find(UserModel.class, id)).thenReturn(user);
        return user;
    }

    private TripModel trip() {
        TripModel trip = new TripModel();
        trip.setId(10L);
        trip.setOwnerId(1L);
        return trip;
    }

    private SuggestionModel city() {
        SuggestionModel city = new SuggestionModel();
        city.setId(4L);
        city.setType(SuggestionType.DESTINATION);
        city.setDestinationKind(DestinationKind.CITY);
        city.setCountry("Sverige");
        city.setName("Malmö");
        return city;
    }
}
