package com.soda2026.journi.tripNote.Dto;

import java.time.LocalDateTime;

public record TripNoteSummeryDto(
        Long id,
        String content,
        LocalDateTime createdAt,
        Long authorId,
        String authorName
) {

}
