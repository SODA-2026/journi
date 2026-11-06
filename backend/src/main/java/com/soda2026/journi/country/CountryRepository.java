package com.soda2026.journi.country;

import com.soda2026.journi.invitation.InvitationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<CountryModel, Long> {
}