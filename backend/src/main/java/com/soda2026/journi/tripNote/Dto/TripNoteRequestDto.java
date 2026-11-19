package com.soda2026.journi.tripNote.Dto;

import jakarta.validation.constraints.NotBlank;

public class TripNoteRequestDto {

    @NotBlank
    private String content;

    public TripNoteRequestDto() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}