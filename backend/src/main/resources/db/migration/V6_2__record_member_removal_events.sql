ALTER TABLE trip_member_departures
    ADD COLUMN event_type VARCHAR(32) NOT NULL DEFAULT 'MEMBER_LEFT',
    ADD COLUMN actor_id BIGINT UNSIGNED NULL,
    ADD COLUMN actor_name VARCHAR(255) NULL,
    ADD COLUMN member_id BIGINT UNSIGNED NULL;
-- Names are snapshots; historical messages survive account deletion or renaming.
-- Old departure rows retain MEMBER_LEFT without inventing unknown user IDs.
