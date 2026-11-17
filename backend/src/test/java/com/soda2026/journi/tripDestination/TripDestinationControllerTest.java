package com.soda2026.journi.tripDestination;

import com.soda2026.journi.city.CityModel;
import com.soda2026.journi.country.CountryModel;
import com.soda2026.journi.security.SecurityConfig;
import com.soda2026.journi.security.UserPrincipal;
import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.user.UserModel;
import com.soda2026.journi.user.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(TripDestinationControllerTest.Config.class)
@WebAppConfiguration
class TripDestinationControllerTest {
    @Configuration
    @EnableWebSecurity
    @EnableWebMvc
    @Import({SecurityConfig.class, TripDestinationController.class, TripDestinationService.class, TripDestinationMapper.class})
    static class Config {
        @Bean UserRepository users() { return mock(UserRepository.class); }
        @Bean TripDestinationRepository repository() { return mock(TripDestinationRepository.class); }
        @Bean EntityManager entityManager() { return mock(EntityManager.class); }
    }

    @Autowired WebApplicationContext context;
    @Autowired TripDestinationRepository repository;
    @Autowired EntityManager entityManager;
    private MockMvc mvc;
    private UserPrincipal principal;

    @BeforeEach void setup() {
        reset(repository, entityManager);
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        var user = new UserModel("owner@example.com", "Owner", "hash", null);
        user.setId(42L);
        principal = new UserPrincipal(user);
        when(entityManager.find(UserModel.class, 42L)).thenReturn(user);
    }

    @Test void rejectsInvalidCityAndOrderBeforeSaving() throws Exception {
        for (String body : List.of(
                "{\"cityId\":null,\"orderIndex\":0}",
                "{\"cityId\":0,\"orderIndex\":0}",
                "{\"cityId\":4,\"orderIndex\":null}",
                "{\"cityId\":4,\"orderIndex\":-1}")) {
            mvc.perform(post("/api/trips/10/destinations").with(user(principal)).with(csrf())
                    .contentType("application/json").content(body)).andExpect(status().isBadRequest());
        }
        verifyNoInteractions(entityManager, repository);
    }

    @Test void createsDestinationForAuthenticatedOwner() throws Exception {
        var trip = trip(42L);
        var city = city(4L, "Malmö");
        when(entityManager.find(TripModel.class, 10L)).thenReturn(trip);
        when(entityManager.find(CityModel.class, 4L)).thenReturn(city);
        when(repository.save(any())).thenAnswer(call -> call.getArgument(0));

        mvc.perform(post("/api/trips/10/destinations").with(user(principal)).with(csrf())
                .contentType("application/json").content("{\"cityId\":4,\"orderIndex\":0,\"userId\":99}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(content().json("{\"id\":null,\"tripId\":10,\"cityId\":4,\"orderIndex\":0}"));
        verify(repository).save(argThat(destination -> destination.getTrip() == trip
                && destination.getCity() == city && destination.getOrderIndex() == 0));
        verify(entityManager).find(UserModel.class, 42L);
        verify(entityManager, never()).find(UserModel.class, 99L);
    }

    @Test void returnsDestinationsInCurrentResponseFormat() throws Exception {
        var trip = trip(42L);
        when(repository.findAllByTrip_IdOrderByOrderIndexAsc(10L)).thenReturn(List.of(
                new TripDestinationModel(trip, city(4L, "Malmö"), 0),
                new TripDestinationModel(trip, city(5L, "Stockholm"), 1)));
        mvc.perform(get("/api/trips/10/destinations").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"id":null,"tripId":10,"cityId":4,"orderIndex":0},
                         {"id":null,"tripId":10,"cityId":5,"orderIndex":1}]
                        """))
                .andExpect(jsonPath("$[0].cityId").value(4))
                .andExpect(jsonPath("$[1].cityId").value(5));
        verify(repository).findAllByTrip_IdOrderByOrderIndexAsc(10L);
        verify(repository, never()).save(any());
    }

    @Test void rejectsPostByNonOwner() throws Exception {
        when(entityManager.find(TripModel.class, 10L)).thenReturn(trip(7L));
        mvc.perform(post("/api/trips/10/destinations").with(user(principal)).with(csrf())
                .contentType("application/json").content("{\"cityId\":4,\"orderIndex\":0}"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(repository);
    }

    @Test void rejectsUserWhoNoLongerExists() throws Exception {
        when(entityManager.find(UserModel.class, 42L)).thenReturn(null);
        mvc.perform(post("/api/trips/10/destinations").with(user(principal)).with(csrf())
                .contentType("application/json").content("{\"cityId\":4,\"orderIndex\":0}"))
                .andExpect(status().isNotFound());
        verifyNoInteractions(repository);
    }

    @Test void requiresAuthenticationForReadingAndWriting() throws Exception {
        mvc.perform(get("/api/trips/10/destinations")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/trips/10/destinations").with(csrf())
                .contentType("application/json").content("{\"cityId\":4,\"orderIndex\":0}"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(entityManager, repository);
    }

    @Test void rejectsMutationWithoutCsrf() throws Exception {
        mvc.perform(post("/api/trips/10/destinations").with(user(principal))
                .contentType("application/json").content("{\"cityId\":4,\"orderIndex\":0}"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(entityManager, repository);
    }

    private TripModel trip(Long ownerId) {
        var trip = new TripModel(); trip.setId(10L); trip.setOwnerId(ownerId); return trip;
    }
    private CityModel city(Long id, String name) {
        var country = new CountryModel(); country.setId(1L); country.setName("Sverige");
        var city = new CityModel(); city.setId(id); city.setName(name); city.setCountry(country); return city;
    }
}
