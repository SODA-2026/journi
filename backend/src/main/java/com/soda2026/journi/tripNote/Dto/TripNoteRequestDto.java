package com.soda2026.journi.tripNote.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TripNoteRequestDto {

    @NotNull
    private Long tripId;

    @NotBlank
    private String content;

    public TripNoteRequestDto() {
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}