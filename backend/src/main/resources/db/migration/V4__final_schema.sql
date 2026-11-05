-- V4: Droppar det gamla schemat (V1-V3) och skapar det slutgiltiga schemat från noll.
-- VARNING: all befintlig data i de gamla tabellerna raderas.
-- Kräver MySQL 8.0.16+ (CHECK constraints).

-- =====================================================
-- 1. Droppa gamla tabeller (barn före föräldrar)
-- =====================================================
DROP TABLE IF EXISTS activity_votes;
DROP TABLE IF EXISTS activities;
DROP TABLE IF EXISTS trip_destinations;
DROP TABLE IF EXISTS notes;
DROP TABLE IF EXISTS password_reset_tokens;
DROP TABLE IF EXISTS invitations;
DROP TABLE IF EXISTS trip_members;
DROP TABLE IF EXISTS suggestions;
DROP TABLE IF EXISTS trips;
DROP TABLE IF EXISTS users;

-- =====================================================
-- 2. Skapa nytt schema
-- =====================================================


-- -----------------------------------------------------
-- users
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `profile_image` VARCHAR(255) NULL DEFAULT NULL,
  `last_login` DATETIME NULL DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_users_email` (`email`)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- trips
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `trips` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `owner_id` BIGINT UNSIGNED NOT NULL,
  `title` VARCHAR(150) NOT NULL,
  `start_date` DATE NOT NULL,
  `end_date` DATE NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `ix_trips_owner_id` (`owner_id`),
  CONSTRAINT `chk_trips_dates` CHECK (`end_date` >= `start_date`),
  CONSTRAINT `fk_trips_owner`
    FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- countries
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `countries` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- cities
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cities` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `country_id` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `ix_cities_country_id` (`country_id`),
  CONSTRAINT `fk_cities_country`
    FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- trip_destinations
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `trip_destinations` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `trip_id` BIGINT UNSIGNED NOT NULL,
  `city_id` BIGINT UNSIGNED NOT NULL,
  `order_index` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_trip_destinations_order` (`trip_id`, `order_index`),
  INDEX `ix_trip_destinations_city_id` (`city_id`),
  CONSTRAINT `fk_trip_destinations_trip`
    FOREIGN KEY (`trip_id`) REFERENCES `trips` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_trip_destinations_city`
    FOREIGN KEY (`city_id`) REFERENCES `cities` (`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- places
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `places` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `short_description` VARCHAR(100) NULL DEFAULT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `city_id` BIGINT UNSIGNED NOT NULL,
  `address` VARCHAR(255) NULL DEFAULT NULL,
  `category` ENUM('TOURIST_ATTRACTION', 'FOOD', 'BAR', 'FAMILY', 'SPORT') NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `ix_places_city_id` (`city_id`),
  CONSTRAINT `fk_places_city`
    FOREIGN KEY (`city_id`) REFERENCES `cities` (`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- trip_activities
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `trip_activities` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `place_id` BIGINT UNSIGNED NULL DEFAULT NULL,
  `trip_destination_id` BIGINT UNSIGNED NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `activity_date` DATE NULL DEFAULT NULL,
  `start_time` TIME NULL DEFAULT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `status` ENUM('PROPOSED', 'PLANNED', 'CANCELLED') NOT NULL DEFAULT 'PLANNED',
  PRIMARY KEY (`id`),
  INDEX `ix_activities_destination_date_time` (`trip_destination_id`, `activity_date`, `start_time`),
  INDEX `ix_activities_place_id` (`place_id`),
  CONSTRAINT `fk_activities_trip_destination`
    FOREIGN KEY (`trip_destination_id`) REFERENCES `trip_destinations` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_activities_place`
    FOREIGN KEY (`place_id`) REFERENCES `places` (`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- invitations
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `invitations` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `trip_id` BIGINT UNSIGNED NOT NULL,
  `inviter_id` BIGINT UNSIGNED NOT NULL,
  `invitee_id` BIGINT UNSIGNED NOT NULL,
  `status` ENUM('PENDING', 'ACCEPTED', 'DECLINED') NOT NULL DEFAULT 'PENDING',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `responded_at` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_invitations_trip_invitee` (`trip_id`, `invitee_id`),
  INDEX `ix_invitations_inviter_id` (`inviter_id`),
  INDEX `ix_invitations_invitee_status` (`invitee_id`, `status`),
  CONSTRAINT `fk_invitations_invitee`
    FOREIGN KEY (`invitee_id`) REFERENCES `users` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_invitations_inviter`
    FOREIGN KEY (`inviter_id`) REFERENCES `users` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_invitations_trip`
    FOREIGN KEY (`trip_id`) REFERENCES `trips` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- trip_notes
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `trip_notes` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `trip_id` BIGINT UNSIGNED NOT NULL,
  `author_id` BIGINT UNSIGNED NULL DEFAULT NULL,
  `content` TEXT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `ix_notes_trip_id` (`trip_id`),
  INDEX `ix_notes_author_id` (`author_id`),
  CONSTRAINT `fk_notes_author`
    FOREIGN KEY (`author_id`) REFERENCES `users` (`id`)
    ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_notes_trip`
    FOREIGN KEY (`trip_id`) REFERENCES `trips` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- password_reset_tokens
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `password_reset_tokens` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `token_hash` CHAR(64) NOT NULL,
  `expires_at` DATETIME NOT NULL,
  `used_at` DATETIME NULL DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_password_reset_tokens_hash` (`token_hash`),
  INDEX `ix_password_reset_tokens_user_expiry` (`user_id`, `expires_at`),
  CONSTRAINT `fk_password_reset_tokens_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- trip_members
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `trip_members` (
  `trip_id` BIGINT UNSIGNED NOT NULL,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `role` ENUM('OWNER', 'MEMBER') NOT NULL DEFAULT 'MEMBER',
  `joined_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`trip_id`, `user_id`),
  INDEX `ix_trip_members_user_id` (`user_id`),
  CONSTRAINT `fk_trip_members_trip`
    FOREIGN KEY (`trip_id`) REFERENCES `trips` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_trip_members_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
