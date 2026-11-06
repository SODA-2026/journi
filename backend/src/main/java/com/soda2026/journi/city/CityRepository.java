package com.soda2026.journi.city;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<CityModel, Long> {
    List<CityModel> findAllByCountryId(Long countryId);
}
