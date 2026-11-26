        ALTER TABLE trip_activities
        ADD COLUMN created_by BIGINT UNSIGNED NULL,
        ADD CONSTRAINT fk_trip_activities_created_by
        FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL;