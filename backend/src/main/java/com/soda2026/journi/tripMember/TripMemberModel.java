package com.soda2026.journi.tripMember;

import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.user.UserModel;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "trip_members")
public class TripMemberModel {

    @EmbeddedId
    private TripMemberId id;

    @MapsId("tripId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private TripModel trip;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Column(name = "joined_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime joinedAt;

    protected TripMemberModel() {
    }

    public TripMemberModel(TripModel trip, UserModel user) {
        this.trip = Objects.requireNonNull(trip, "Trip is required");
        this.user = Objects.requireNonNull(user, "User is required");
        this.id = new TripMemberId(trip.getId(), user.getId());
    }

    public TripMemberId getId() {
        return id;
    }

    public TripModel getTrip() {
        return trip;
    }

    public UserModel getUser() {
        return user;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }


}

/*
 * Each row grants the user membership in one trip, with the same rights as other members.
 * The owner is stored separately in trips.owner_id and has access without a membership row.
 * New memberships are created when invitations are accepted.
 */
