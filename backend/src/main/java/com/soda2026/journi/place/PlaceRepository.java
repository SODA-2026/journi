package com.soda2026.journi.place;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<PlaceModel, Long> {

    List<PlaceModel> findByCityId(Long cityId);

}
