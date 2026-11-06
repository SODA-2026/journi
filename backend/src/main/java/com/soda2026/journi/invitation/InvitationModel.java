package com.soda2026.journi.invitation;

import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.user.UserModel;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
public class InvitationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private TripModel trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    private UserModel inviter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id", nullable = false)
    private UserModel invitee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    public InvitationModel() {
    }

    public InvitationModel(TripModel trip, UserModel inviter, UserModel invitee) {
        this.trip = trip;
        this.inviter = inviter;
        this.invitee = invitee;
        this.status = InvitationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public TripModel getTrip() {
        return trip;
    }

    public UserModel getInviter() {
        return inviter;
    }

    public UserModel getInvitee() {
        return invitee;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }
}
