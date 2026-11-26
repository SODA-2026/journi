package com.soda2026.journi.tripActivity;

import com.soda2026.journi.tripActivity.dto.TripActivityResponseDTO;
import com.soda2026.journi.tripDestination.TripDestinationModel;
import com.soda2026.journi.user.UserModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TripActivityMapperTest {

    private final TripActivityMapper mapper = new TripActivityMapper();

    private TripActivityModel activityWithDestination() {
        TripDestinationModel destination = mock(TripDestinationModel.class);
        when(destination.getId()).thenReturn(1L);

        TripActivityModel activity = new TripActivityModel();
        activity.setTripDestination(destination);
        activity.setName("Middag");
        return activity;
    }

    @Test
    void toDto_handlesMissingCreator() {
        TripActivityModel activity = activityWithDestination();

        TripActivityResponseDTO dto = mapper.toDto(activity);

        assertNull(dto.createdById());
        assertNull(dto.createdByName());
    }

    @Test
    void toDto_includesCreatorIdAndName() {
        UserModel user = new UserModel();
        user.setId(7L);
        user.setName("Dhannea Pettersson");

        TripActivityModel activity = activityWithDestination();
        activity.setCreatedBy(user);

        TripActivityResponseDTO dto = mapper.toDto(activity);

        assertEquals(7L, dto.createdById());
        assertEquals("Dhannea Pettersson", dto.createdByName());
    }
}
