-- Version 6.1 follows V6 and precedes the pending KAN-104 migration V7.
CREATE TABLE trip_member_departures (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    trip_id BIGINT UNSIGNED NOT NULL,
    member_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    INDEX ix_trip_departures_trip_created (trip_id, created_at, id),
    CONSTRAINT fk_trip_departures_trip FOREIGN KEY (trip_id)
        REFERENCES trips (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
