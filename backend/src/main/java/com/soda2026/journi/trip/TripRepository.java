 package com.soda2026.journi.trip;

 import org.springframework.data.jpa.repository.JpaRepository;
 import org.springframework.stereotype.Repository;

 import java.util.List;

 @Repository
 public interface TripRepository extends JpaRepository<TripModel, Long> {
     List<TripModel> findAllByOwnerIdOrderByStartDateDesc(Long ownerId);
 }
