package com.soda2026.journi.tripMember;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "trip_member_events")
public class TripMemberEventModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Column(name = "member_name", nullable = false)
    private String memberName;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "event_type", nullable = false, length = 32)
    private TripMemberEventType eventType = TripMemberEventType.MEMBER_LEFT;

    @Column(name = "actor_id")
    private Long actorId;

    @Column(name = "actor_name")
    private String actorName;

    @Column(name = "member_id")
    private Long memberId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected TripMemberEventModel() {
    }

    public TripMemberEventModel(Long tripId, String memberName) {
        this.tripId = tripId;
        this.memberName = memberName;
    }

    public TripMemberEventModel(
            Long tripId,
            Long memberId,
            String memberName,
            TripMemberEventType eventType,
            Long actorId,
            String actorName
    ) {
        this(tripId, memberName);
        this.memberId = memberId;
        this.eventType = eventType;
        this.actorId = actorId;
        this.actorName = actorName;
    }

    public Long getId() {
        return id;
    }

    public Long getTripId() {
        return tripId;
    }

    public String getMemberName() {
        return memberName;
    }

    public TripMemberEventType getEventType() {
        return eventType;
    }

    public Long getActorId() {
        return actorId;
    }

    public String getActorName() {
        return actorName;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
