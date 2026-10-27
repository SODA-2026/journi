package com.soda2026.journi.suggestion;

import java.time.Instant;

public record SuggestionDTO(
        Long id,
        String type,
        String destinationKind,
        Long parentSuggestionId,
        String name,
        String country,
        String region,
        String category,
        String shortDescription,
        String description,
        String bestTravelTime,
        String imageUrl,
        Instant createdAt,
        Instant updatedAt
) {
}
