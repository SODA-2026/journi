package com.soda2026.journi.tripMember;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "trip_member_departures")
public class TripDepartureModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Column(name = "member_name", nullable = false)
    private String memberName;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected TripDepartureModel() {
    }

    public TripDepartureModel(Long tripId, String memberName) {
        this.tripId = tripId;
        this.memberName = memberName;
    }

    public Long getId() { return id; }
    public Long getTripId() { return tripId; }
    public String getMemberName() { return memberName; }
    public Instant getCreatedAt() { return createdAt; }
}
