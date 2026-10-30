package com.soda2026.journi.trip.dto;

public class TripResponse {

    private String title;

    public TripResponse(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
