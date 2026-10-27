package com.soda2026.journi.passwordResetToken;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenModel, Long> {
    Optional<PasswordResetTokenModel> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
            UPDATE PasswordResetTokenModel token
            SET token.usedAt = :now
            WHERE token.tokenHash = :tokenHash
              AND token.usedAt IS NULL
              AND token.expiresAt > :now
            """)
    int markUsedIfValid(@Param("tokenHash") String tokenHash, @Param("now") Instant now);
}