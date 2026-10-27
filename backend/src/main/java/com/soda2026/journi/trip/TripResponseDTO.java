package com.soda2026.journi.trip;

public class TripResponseDTO {

    private String title;

    public TripResponseDTO(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
