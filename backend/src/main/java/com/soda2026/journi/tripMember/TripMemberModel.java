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

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private TripMemberRole role = TripMemberRole.EDITOR;

    @Column(name = "joined_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    protected TripMemberModel() {
    }

    public TripMemberModel(TripModel trip, UserModel user, TripMemberRole role) {
        this.trip = Objects.requireNonNull(trip, "Trip is required");
        this.user = Objects.requireNonNull(user, "User is required");
        this.id = new TripMemberId(trip.getId(), user.getId());
        this.role = Objects.requireNonNull(role, "Role is required");
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

    public TripMemberRole getRole() {
        return role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setRole(TripMemberRole role) {
        this.role = Objects.requireNonNull(role, "Role is required");
    }
}

/*
 * Medlemskap i en resa
 * Kopplar en befintlig resa till en befintlig användare genom en sammansatt nyckel.
 * @MapsId kopplar relationerna till motsvarande fält i TripMemberId.
 * EDITOR och VIEWER lagras som text. Databasen hanterar joined_at och updated_at;
 * aktuella tidsvärden kan behöva läsas om efter sparande.
 * Medlemskap ska skapas när en inbjudan accepteras, i samma transaktion.
 * Modellen kontrollerar inte inbjudningar eller behörighet; det är tjänsternas ansvar.
 * Förutsätter färdiga JPA-entiteter TripModel och UserModel med getId().
 *
 * Участие в поездке
 * Связывает существующую поездку с существующим пользователем составным ключом.
 * @MapsId связывает отношения с соответствующими полями TripMemberId.
 * EDITOR и VIEWER хранятся как текст. Датами joined_at и updated_at управляет база;
 * после сохранения для получения актуальных дат может потребоваться повторное чтение.
 * Участник должен создаваться при принятии приглашения в той же транзакции.
 * Модель не проверяет приглашения или права; это ответственность сервисов.
 * Ожидает готовые JPA-сущности TripModel и UserModel с getId().
 */
