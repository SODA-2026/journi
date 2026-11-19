package com.soda2026.journi.tripNote;

import com.soda2026.journi.security.SecurityConfig;
import com.soda2026.journi.security.UserPrincipal;
import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.trip.TripRepository;
import com.soda2026.journi.tripMember.TripMemberRepository;
import com.soda2026.journi.user.UserModel;
import com.soda2026.journi.user.UserRepository;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(TripNoteControllerTest.Config.class)
@WebAppConfiguration
class TripNoteControllerTest {
    @Configuration
    @EnableWebSecurity
    @EnableWebMvc
    @Import({SecurityConfig.class, TripNoteController.class, TripNoteService.class, TripNoteMapper.class})
    static class Config {
        @Bean TripNoteRepository notes() { return mock(TripNoteRepository.class); }
        @Bean TripRepository trips() { return mock(TripRepository.class); }
        @Bean UserRepository users() { return mock(UserRepository.class); }
        @Bean TripMemberRepository members() { return mock(TripMemberRepository.class); }
    }

    @Autowired WebApplicationContext context;
    @Autowired TripNoteRepository notes;
    @Autowired TripRepository trips;
    @Autowired UserRepository users;
    @Autowired TripMemberRepository members;

    private MockMvc mvc;
    private UserPrincipal principal;

    @BeforeEach
    void setup() {
        reset(notes, trips, users, members);
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        principal = new UserPrincipal(userModel(42L));
    }

    @Test
    void requiresAuthenticationForReadingAndWriting() throws Exception {
        mvc.perform(get("/trips/10/notes")).andExpect(status().isUnauthorized());
        mvc.perform(post("/trips/10/notes").with(csrf())
                .contentType("application/json").content("{\"content\":\"Note\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(delete("/trips/10/notes/5").with(csrf())).andExpect(status().isUnauthorized());
        verifyNoInteractions(notes, trips, users, members);
    }

    @Test
    void rejectsMutationsWithoutCsrf() throws Exception {
        mvc.perform(post("/trips/10/notes").with(user(principal))
                .contentType("application/json").content("{\"content\":\"Note\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/trips/10/notes/5").with(user(principal)))
                .andExpect(status().isForbidden());
        verifyNoInteractions(notes, trips, users, members);
    }

    @Test
    void rejectsBlankContentBeforeCallingService() throws Exception {
        for (String body : List.of("{\"content\":\"\"}", "{\"content\":\"   \"}", "{}")) {
            mvc.perform(post("/trips/10/notes").with(user(principal)).with(csrf())
                    .contentType("application/json").content(body))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(notes, trips, users, members);
    }

    @Test
    void createsNoteForOwnerUsingSessionIdentityAsAuthor() throws Exception {
        givenTrip(42L);
        UserModel owner = userModel(42L);
        when(users.getReferenceById(42L)).thenReturn(owner);
        when(notes.save(any())).thenAnswer(call -> call.getArgument(0));

        mvc.perform(post("/trips/10/notes").with(user(principal)).with(csrf())
                .contentType("application/json")
                .content("{\"content\":\"Pack bags\",\"authorId\":99,\"tripId\":999}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.tripId").value(10))
                .andExpect(jsonPath("$.authorId").value(42))
                .andExpect(jsonPath("$.content").value("Pack bags"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        verify(notes).save(argThat(note -> note.getTrip().getId().equals(10L)
                && note.getAuthor() == owner
                && note.getContent().equals("Pack bags")));
        verify(users).getReferenceById(42L);
        verify(users, never()).getReferenceById(99L);
    }

    @Test
    void allowsTripMemberToCreateNote() throws Exception {
        TripModel trip = givenTrip(7L);
        when(members.existsById_TripIdAndId_UserId(10L, 42L)).thenReturn(true);
        when(users.getReferenceById(42L)).thenReturn(userModel(42L));
        when(notes.save(any())).thenAnswer(call -> call.getArgument(0));

        mvc.perform(post("/trips/10/notes").with(user(principal)).with(csrf())
                .contentType("application/json").content("{\"content\":\"Hello\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tripId").value(10))
                .andExpect(jsonPath("$.authorId").value(42));

        verify(notes).save(argThat(note -> note.getTrip() == trip));
    }

    @Test
    void rejectsCreationByOutsider() throws Exception {
        givenTrip(7L);

        mvc.perform(post("/trips/10/notes").with(user(principal)).with(csrf())
                .contentType("application/json").content("{\"content\":\"Hello\"}"))
                .andExpect(status().isForbidden());

        verify(notes, never()).save(any());
        verify(users, never()).getReferenceById(anyLong());
    }

    @Test
    void returnsNotFoundWhenCreatingNoteForMissingTrip() throws Exception {
        mvc.perform(post("/trips/10/notes").with(user(principal)).with(csrf())
                .contentType("application/json").content("{\"content\":\"Hello\"}"))
                .andExpect(status().isNotFound());

        verify(notes, never()).save(any());
    }

    @Test
    void listsNotesForTripMember() throws Exception {
        TripModel trip = givenTrip(7L);
        when(members.existsById_TripIdAndId_UserId(10L, 42L)).thenReturn(true);
        when(notes.findAllByTrip_IdOrderByUpdatedAtDesc(10L)).thenReturn(List.of(
                new TripNoteModel(trip, userModel(42L), "Newest"),
                new TripNoteModel(trip, userModel(7L), "Older")));

        mvc.perform(get("/trips/10/notes").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("Newest"))
                .andExpect(jsonPath("$[1].content").value("Older"))
                .andExpect(jsonPath("$[0].id").doesNotExist())
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty());

        verify(notes).findAllByTrip_IdOrderByUpdatedAtDesc(10L);
    }

    @Test
    void rejectsListingByOutsider() throws Exception {
        givenTrip(7L);

        mvc.perform(get("/trips/10/notes").with(user(principal)))
                .andExpect(status().isForbidden());

        verify(notes, never()).findAllByTrip_IdOrderByUpdatedAtDesc(anyLong());
    }

    @Test
    void authorCanDeleteOwnNote() throws Exception {
        TripModel trip = givenTrip(7L);
        TripNoteModel note = new TripNoteModel(trip, userModel(42L), "Mine");
        when(notes.findById(5L)).thenReturn(Optional.of(note));
        when(members.existsById_TripIdAndId_UserId(10L, 42L)).thenReturn(true);

        mvc.perform(delete("/trips/10/notes/5").with(user(principal)).with(csrf()))
                .andExpect(status().isNoContent());

        verify(notes).delete(note);
    }

    @Test
    void removedAuthorCannotDeleteOwnNote() throws Exception {
        TripModel trip = givenTrip(7L);
        TripNoteModel note = new TripNoteModel(trip, userModel(42L), "Was mine");
        when(notes.findById(5L)).thenReturn(Optional.of(note));
        when(members.existsById_TripIdAndId_UserId(10L, 42L)).thenReturn(false);

        mvc.perform(delete("/trips/10/notes/5").with(user(principal)).with(csrf()))
                .andExpect(status().isForbidden());

        verify(notes, never()).delete(any());
    }

    @Test
    void tripOwnerCanDeleteAnotherAuthorsNote() throws Exception {
        TripModel trip = givenTrip(42L);
        TripNoteModel note = new TripNoteModel(trip, userModel(7L), "Theirs");
        when(notes.findById(5L)).thenReturn(Optional.of(note));

        mvc.perform(delete("/trips/10/notes/5").with(user(principal)).with(csrf()))
                .andExpect(status().isNoContent());

        verify(notes).delete(note);
    }

    @Test
    void rejectsDeletionByNonAuthorNonOwner() throws Exception {
        TripModel trip = givenTrip(7L);
        TripNoteModel note = new TripNoteModel(trip, userModel(8L), "Not yours");
        when(notes.findById(5L)).thenReturn(Optional.of(note));
        when(members.existsById_TripIdAndId_UserId(10L, 42L)).thenReturn(true);

        mvc.perform(delete("/trips/10/notes/5").with(user(principal)).with(csrf()))
                .andExpect(status().isForbidden());

        verify(notes, never()).delete(any());
    }

    @Test
    void returnsNotFoundWhenNoteBelongsToAnotherTrip() throws Exception {
        TripModel otherTrip = new TripModel();
        otherTrip.setId(20L);
        otherTrip.setOwnerId(42L);
        when(notes.findById(5L)).thenReturn(Optional.of(
                new TripNoteModel(otherTrip, userModel(42L), "Other trip")));

        mvc.perform(delete("/trips/10/notes/5").with(user(principal)).with(csrf()))
                .andExpect(status().isNotFound());

        verify(notes, never()).delete(any());
    }

    @Test
    void returnsNotFoundWhenDeletingMissingNote() throws Exception {
        mvc.perform(delete("/trips/10/notes/5").with(user(principal)).with(csrf()))
                .andExpect(status().isNotFound());

        verify(notes, never()).delete(any());
    }

    private TripModel givenTrip(Long ownerId) {
        TripModel trip = new TripModel();
        trip.setId(10L);
        trip.setOwnerId(ownerId);
        when(trips.findById(10L)).thenReturn(Optional.of(trip));
        return trip;
    }

    private UserModel userModel(Long id) {
        UserModel user = new UserModel("user" + id + "@example.com", "User " + id, "hash", null);
        user.setId(id);
        return user;
    }
}
