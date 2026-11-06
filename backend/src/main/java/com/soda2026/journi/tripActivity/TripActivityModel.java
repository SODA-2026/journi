package com.soda2026.journi.tripActivity;

import com.soda2026.journi.place.PlaceModel;
import com.soda2026.journi.tripDestination.TripDestinationModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "trip_activities")
public class TripActivityModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private PlaceModel place;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_destination_id", nullable = false)
    private TripDestinationModel tripDestination;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "activity_date")
    private LocalDate activityDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TripActivityStatus status = TripActivityStatus.PLANNED;

    public TripActivityModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlaceModel getPlace() {
        return place;
    }

    public void setPlace(PlaceModel place) {
        this.place = place;
    }

    public TripDestinationModel getTripDestination() {
        return tripDestination;
    }

    public void setTripDestination(TripDestinationModel tripDestination) {
        this.tripDestination = tripDestination;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(LocalDate activityDate) {
        this.activityDate = activityDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TripActivityStatus getStatus() {
        return status;
    }

    public void setStatus(TripActivityStatus status) {
        this.status = status;
    }
}