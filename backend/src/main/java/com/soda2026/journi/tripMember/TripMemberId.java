package com.soda2026.journi.tripMember;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TripMemberId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    protected TripMemberId() {
    }

    public TripMemberId(Long tripId, Long userId) {
        this.tripId = Objects.requireNonNull(tripId, "Trip ID is required");
        this.userId = Objects.requireNonNull(userId, "User ID is required");
    }

    public Long getTripId() {
        return tripId;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TripMemberId that)) {
            return false;
        }
        return Objects.equals(tripId, that.tripId)
                && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId, userId);
    }
}

/*
 * Sammansatt nyckel
 * Tabellen använder kombinationen trip_id och user_id som primärnyckel.
 * Samma användare kan bara vara medlem en gång i samma resa.
 * @Embeddable gör nyckeln användbar med @EmbeddedId i modellen.
 * equals och hashCode jämför båda delarna av nyckeln; Serializable krävs av JPA.
 *
 * Составной ключ
 * Первичный ключ таблицы состоит из trip_id и user_id.
 * Пользователь может быть участником одной поездки только один раз.
 * @Embeddable позволяет использовать ключ через @EmbeddedId в модели.
 * equals и hashCode учитывают обе части ключа; Serializable нужен JPA.
 */
