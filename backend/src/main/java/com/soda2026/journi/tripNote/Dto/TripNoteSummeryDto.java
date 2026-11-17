package com.soda2026.journi.tripNote.Dto;

import java.time.LocalDateTime;

public record TripNoteSummeryDto(
        String content,
        LocalDateTime createdAt
) {

}
