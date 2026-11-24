package com.soda2026.journi.tripMember.dto;

import com.soda2026.journi.tripMember.TripMemberEventModel;
import com.soda2026.journi.tripMember.TripMemberEventType;

import java.time.Instant;

public record TripMemberEventResponse(
        Long id,
        String memberName,
        Instant createdAt,
        TripMemberEventType eventType,
        Long actorId,
        String actorName,
        Long memberId
) {
    public static TripMemberEventResponse from(TripMemberEventModel event) {
        return new TripMemberEventResponse(
                event.getId(),
                event.getMemberName(),
                event.getCreatedAt(),
                event.getEventType(),
                event.getActorId(),
                event.getActorName(),
                event.getMemberId()
        );
    }
}
