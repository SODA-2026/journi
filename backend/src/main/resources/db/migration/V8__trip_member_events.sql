-- Preserve history in development databases that already applied the earlier
-- KAN-111 migrations; other databases create the final table after V7.
CREATE TABLE IF NOT EXISTS trip_member_events (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    trip_id BIGINT UNSIGNED NOT NULL,
    member_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    event_type VARCHAR(32) NOT NULL DEFAULT 'MEMBER_LEFT',
    actor_id BIGINT UNSIGNED NULL,
    actor_name VARCHAR(255) NULL,
    member_id BIGINT UNSIGNED NULL,
    PRIMARY KEY (id),
    INDEX ix_trip_departures_trip_created (trip_id, created_at, id),
    CONSTRAINT fk_trip_departures_trip FOREIGN KEY (trip_id)
        REFERENCES trips (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
-- Names are snapshots; historical messages survive account deletion or renaming.
