-- Reseplanerare: balanced MVP schema (Jira-aligned revision)
-- MySQL 8.0+
-- Suggestion destinations and activities share one table.

SET NAMES utf8mb4;

CREATE TABLE users (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    profile_image   VARCHAR(255) NULL,
    last_login      DATETIME NULL,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                    ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE trips (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    owner_id        BIGINT UNSIGNED NOT NULL,
    title           VARCHAR(150) NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                    ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY ix_trips_owner_id (owner_id),
    CONSTRAINT ck_trips_date_range CHECK (end_date >= start_date),
    CONSTRAINT fk_trips_owner
        FOREIGN KEY (owner_id) REFERENCES users (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE invitations (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    trip_id         BIGINT UNSIGNED NOT NULL,
    inviter_id      BIGINT UNSIGNED NOT NULL,
    invitee_id      BIGINT UNSIGNED NOT NULL,
    status          ENUM('PENDING', 'ACCEPTED', 'DECLINED') NOT NULL
                    DEFAULT 'PENDING',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    responded_at    DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_invitations_trip_invitee (trip_id, invitee_id),
    KEY ix_invitations_inviter_id (inviter_id),
    KEY ix_invitations_invitee_status (invitee_id, status),
    CONSTRAINT fk_invitations_trip
        FOREIGN KEY (trip_id) REFERENCES trips (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_invitations_inviter
        FOREIGN KEY (inviter_id) REFERENCES users (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_invitations_invitee
        FOREIGN KEY (invitee_id) REFERENCES users (id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE trip_members (
    trip_id         BIGINT UNSIGNED NOT NULL,
    user_id         BIGINT UNSIGNED NOT NULL,
    role            ENUM('EDITOR', 'VIEWER') NOT NULL DEFAULT 'EDITOR',
    joined_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                    ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (trip_id, user_id),
    KEY ix_trip_members_user_id (user_id),
    CONSTRAINT fk_trip_members_trip
        FOREIGN KEY (trip_id) REFERENCES trips (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_trip_members_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE suggestions (
    id                      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    type                    ENUM('DESTINATION', 'ACTIVITY') NOT NULL,
    destination_kind        ENUM('COUNTRY', 'CITY', 'REGION', 'PLACE') NULL,
    parent_suggestion_id    BIGINT UNSIGNED NULL,
    name                    VARCHAR(150) NOT NULL,
    country                 VARCHAR(100) NULL,
    region                  VARCHAR(150) NULL,
    category                VARCHAR(100) NULL,
    short_description       VARCHAR(500) NOT NULL,
    description             TEXT NULL,
    best_travel_time        VARCHAR(150) NULL,
    image_url               VARCHAR(255) NULL,
    created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                            ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY ix_suggestions_type_kind (type, destination_kind),
    KEY ix_suggestions_parent (parent_suggestion_id),
    CONSTRAINT ck_suggestions_type_fields CHECK (
        (type = 'DESTINATION'
            AND destination_kind IS NOT NULL
            AND country IS NOT NULL)
        OR
        (type = 'ACTIVITY'
            AND destination_kind IS NULL
            AND category IS NOT NULL)
    ),
    CONSTRAINT fk_suggestions_parent
        FOREIGN KEY (parent_suggestion_id) REFERENCES suggestions (id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE trip_destinations (
    id                      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    trip_id                 BIGINT UNSIGNED NOT NULL,
    source_suggestion_id    BIGINT UNSIGNED NULL,
    country                 VARCHAR(100) NOT NULL,
    place_name              VARCHAR(150) NULL,
    order_index             INT UNSIGNED NOT NULL,
    created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                            ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_trip_destinations_order (trip_id, order_index),
    KEY ix_trip_destinations_source (source_suggestion_id),
    CONSTRAINT ck_trip_destinations_country CHECK (TRIM(country) <> ''),
    CONSTRAINT ck_trip_destinations_place CHECK (
        place_name IS NULL OR TRIM(place_name) <> ''
    ),
    CONSTRAINT fk_trip_destinations_trip
        FOREIGN KEY (trip_id) REFERENCES trips (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_trip_destinations_source
        FOREIGN KEY (source_suggestion_id) REFERENCES suggestions (id)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE activities (
    id                      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    trip_destination_id     BIGINT UNSIGNED NOT NULL,
    created_by              BIGINT UNSIGNED NULL,
    source_suggestion_id    BIGINT UNSIGNED NULL,
    name                    VARCHAR(150) NOT NULL,
    activity_date           DATE NULL,
    start_time              TIME NULL,
    description             TEXT NULL,
    status                  ENUM('PROPOSED', 'PLANNED', 'CANCELLED') NOT NULL
                            DEFAULT 'PLANNED',
    created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                            ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT ck_activities_schedule CHECK (
        (activity_date IS NULL AND start_time IS NULL)
        OR
        (activity_date IS NOT NULL AND start_time IS NOT NULL)
    ),
    CONSTRAINT ck_activities_planned_has_schedule CHECK (
        status <> 'PLANNED'
        OR (activity_date IS NOT NULL AND start_time IS NOT NULL)
    ),
    KEY ix_activities_destination_date_time
        (trip_destination_id, activity_date, start_time),
    KEY ix_activities_created_by (created_by),
    KEY ix_activities_source (source_suggestion_id),
    CONSTRAINT fk_activities_trip_destination
        FOREIGN KEY (trip_destination_id) REFERENCES trip_destinations (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_activities_created_by
        FOREIGN KEY (created_by) REFERENCES users (id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_activities_source
        FOREIGN KEY (source_suggestion_id) REFERENCES suggestions (id)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE activity_votes (
    activity_id     BIGINT UNSIGNED NOT NULL,
    user_id         BIGINT UNSIGNED NOT NULL,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (activity_id, user_id),
    KEY ix_activity_votes_user_id (user_id),
    CONSTRAINT fk_activity_votes_activity
        FOREIGN KEY (activity_id) REFERENCES activities (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_activity_votes_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE notes (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    trip_id         BIGINT UNSIGNED NOT NULL,
    author_id       BIGINT UNSIGNED NULL,
    content         TEXT NOT NULL,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                    ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY ix_notes_trip_id (trip_id),
    KEY ix_notes_author_id (author_id),
    CONSTRAINT fk_notes_trip
        FOREIGN KEY (trip_id) REFERENCES trips (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_notes_author
        FOREIGN KEY (author_id) REFERENCES users (id)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE password_reset_tokens (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id         BIGINT UNSIGNED NOT NULL,
    token_hash      CHAR(64) NOT NULL,
    expires_at      DATETIME NOT NULL,
    used_at         DATETIME NULL,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_password_reset_tokens_hash (token_hash),
    KEY ix_password_reset_tokens_user_expiry (user_id, expires_at),
    CONSTRAINT fk_password_reset_tokens_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Application/service rules that cannot be expressed as simple MySQL CHECKs:
-- MySQL does not allow CHECK constraints to reference columns used by
-- cascading foreign keys, so these relationship rules must be validated here.
-- 1. DESTINATION suggestions have no parent; ACTIVITY suggestions reference a
--    parent suggestion whose type is DESTINATION.
-- 2. trip_destinations.source_suggestion_id must reference DESTINATION.
-- 3. activities.source_suggestion_id must reference ACTIVITY.
-- 4. When activity_date is present, it must fall between the parent trip's
--    start_date and end_date. PROPOSED activities may be unscheduled.
-- 5. Only the trip owner or an accepted trip member may vote or edit the trip.
-- 6. When an invitation is accepted, insert the invitee into trip_members.
-- 7. A logged-out user's pending KAN-52 selection is retained by the frontend
--    (for example in localStorage) until authentication completes.
-- 8. A trip destination may initially contain only a country; place_name is
--    filled later when the user chooses a city or another specific place.
-- 9. OWNER is determined by trips.owner_id (not by trip_members.role).
--    Owner: manage trip, invites, roles and deletion. EDITOR: edit trip content.
--    VIEWER: read and vote; all operations require backend authorization.
-- 10. PROPOSED may be unscheduled; PLANNED requires both date and time;
--     CANCELLED can retain schedule or be unscheduled. Votes accepted ONLY
--     for PROPOSED, from the owner or trip members. One vote per user/idea.
--     No automatic approval threshold: OWNER/EDITOR decides transition.
-- 11. Account deletion policy (MVP): trips.owner_id uses ON DELETE
--     RESTRICT, so a user who owns any trips cannot be deleted until those
--     trips are explicitly handled. Before deleting the account, the service
--     must either transfer each trip to another consenting member or delete
--     each trip after confirmation. Validate/perform this in a transaction.
--     Deleting a non-owner preserves shared trips; authored notes retain
--     content with author_id=NULL (subject to privacy/product policy).
-- 12. Re-invite after DECLINED: update existing unique invitation row back
--     to PENDING (subject to authorization); do not INSERT a duplicate.
-- 13. Reordering trip_destinations.order_index must avoid temporary UNIQUE
--     conflicts; implement transaction with collision-safe intermediate values.
-- 14. Dates of activities must be checked against owning trip dates at
--     create/update, including when changing trip dates or destination.
-- 15. Validate that reference types are correct: suggestion ACTIVITY ->
--     parent DESTINATION; source_suggestion for trip_destinations ->
--     DESTINATION, and for activities -> ACTIVITY.
-- 16. This is a proposed initial schema; run SQL against MySQL 8.0.16+
--     and validate migration/versioning strategy before team approval.
-- 17. invitations.inviter_id and invitee_id must differ.

-- suggestion.image_url example: /images/destinations/paris.jpg
-- Files are served by the frontend; no suggestion_images table is used.
