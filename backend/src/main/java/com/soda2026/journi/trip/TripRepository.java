package com.soda2026.journi.trip;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<TripModel, Long> {

    @EntityGraph(attributePaths = "owner")
    List<TripModel> findAllByOwnerIdOrderByStartDateDesc(Long ownerId);

    @EntityGraph(attributePaths = "owner")
    @Query("""
            select t from TripModel t
            where t.ownerId = :userId
               or exists (
                   select m from TripMemberModel m
                   where m.id.tripId = t.id and m.id.userId = :userId
               )
            order by t.startDate desc
            """)
    List<TripModel> findAllAccessibleTo(@Param("userId") Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TripModel t where t.id = :id")
    Optional<TripModel> findForUpdate(@Param("id") Long id);
}
