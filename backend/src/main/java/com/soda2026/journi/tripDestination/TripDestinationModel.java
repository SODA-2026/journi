package com.soda2026.journi.tripDestination;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.suggestion.SuggestionModel;

@Entity
@Table(
        name = "trip_destinations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_trip_destinations_order",
                        columnNames = {"trip_id", "order_index"}
                )
        }
)
public class TripDestinationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private TripModel trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_suggestion_id")
    private SuggestionModel sourceSuggestion;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "place_name", length = 150)
    private String placeName;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime updatedAt;

    protected TripDestinationModel() {
    }

    public TripDestinationModel(
            TripModel trip,
            SuggestionModel sourceSuggestion,
            String country,
            String placeName,
            Integer orderIndex
    ) {
        this.trip = trip;
        this.sourceSuggestion = sourceSuggestion;
        this.country = country;
        this.placeName = placeName;
        this.orderIndex = orderIndex;
    }

    public Long getId() {
        return id;
    }

    public TripModel getTrip() {
        return trip;
    }

    public SuggestionModel getSourceSuggestion() {
        return sourceSuggestion;
    }

    public String getCountry() {
        return country;
    }

    public String getPlaceName() {
        return placeName;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setSourceSuggestion(SuggestionModel sourceSuggestion) {
        this.sourceSuggestion = sourceSuggestion;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}

/*
 * Modellens ansvar
 * Representerar en rad i trip_destinations. trip är en obligatorisk ManyToOne-relation;
 * sourceSuggestion är valfri i databasen, men krävs vid skapande via API:et. LAZY skjuter upp laddningen av relaterade objekt.
 * Kombinationen trip_id och order_index är unik inom en resa.
 * Databasen genererar id och hanterar created_at och updated_at. Databasgenererade
 * tidsvärden kan behöva läsas om efter sparande. Den skyddade konstruktorn används av JPA.
 * TripModel och SuggestionModel förutsätts vara JPA-entiteter.
 *
 * Назначение модели
 * Представляет строку trip_destinations. Связь trip обязательна. sourceSuggestion допускает null в базе, но обязателен при создании через API.
 * LAZY позволяет отложить загрузку связанных объектов.
 * Пара trip_id и order_index уникальна: у каждой остановки своя позиция внутри поездки.
 * База создаёт id и управляет created_at и updated_at. Для получения обновлённых дат
 * может потребоваться повторное чтение. Защищённый конструктор нужен JPA.
 * Предполагается, что TripModel и SuggestionModel реализованы как JPA-сущности.
 */
