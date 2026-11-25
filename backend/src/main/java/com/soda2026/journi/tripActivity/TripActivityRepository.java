 package com.soda2026.journi.tripActivity;

 import com.soda2026.journi.tripActivity.TripActivityModel;
 import org.springframework.data.jpa.repository.JpaRepository;

 import java.util.List;

 public interface TripActivityRepository extends JpaRepository<TripActivityModel, Long> {

  List<TripActivityModel> findByTripDestinationIdOrderByActivityDateAscStartTimeAsc(Long tripDestination);

  void deleteAllByTripDestination_Id(Long tripDestinationId);
 }
