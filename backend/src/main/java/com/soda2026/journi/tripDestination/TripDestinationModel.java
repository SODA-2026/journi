package com.soda2026.journi.tripDestination;

import com.soda2026.journi.city.CityModel;
import jakarta.persistence.*;
import com.soda2026.journi.trip.TripModel;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private CityModel city;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    protected TripDestinationModel() {
    }

    public TripDestinationModel(
            TripModel trip,
            CityModel city,
            Integer orderIndex
    ) {
        this.trip = trip;
        this.city = city;
        this.orderIndex = orderIndex;
    }

    public Long getId() {
        return id;
    }

    public TripModel getTrip() {
        return trip;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public CityModel getCity() {
        return city;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}