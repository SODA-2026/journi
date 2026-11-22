package com.soda2026.journi.tripMember.dto;

import com.soda2026.journi.tripMember.TripDepartureModel;
import java.time.Instant;

public record TripDepartureResponse(Long id, String memberName, Instant createdAt) {
    public static TripDepartureResponse from(TripDepartureModel departure) {
        return new TripDepartureResponse(
                departure.getId(), departure.getMemberName(), departure.getCreatedAt());
    }
}
