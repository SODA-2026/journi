# Databasschema
 
Databas: MySQL. Schemat bygger på gruppens user stories och är normaliserat.
Alla primärnycklar är `BIGINT AUTO_INCREMENT` utom i kopplingstabellerna `trip_members` och `activity_votes`, som har sammansatta primärnycklar.
 
## Diagram
 
```mermaid
erDiagram
    USERS ||--o{ TRIPS : "äger"
    USERS ||--o{ TRIP_MEMBERS : "är medlem via"
    TRIPS ||--o{ TRIP_MEMBERS : "har"
    USERS ||--o{ INVITATIONS : "skickar (inviter)"
    USERS ||--o{ INVITATIONS : "tar emot (invitee)"
    TRIPS ||--o{ INVITATIONS : "har"
    TRIPS ||--o{ TRIP_DESTINATIONS : "har"
    TRIP_DESTINATIONS ||--o{ ACTIVITIES : "har"
    USERS |o--o{ ACTIVITIES : "skapar"
    ACTIVITIES ||--o{ ACTIVITY_VOTES : "får"
    USERS ||--o{ ACTIVITY_VOTES : "röstar"
    TRIPS ||--o{ NOTES : "har"
    USERS ||--o{ NOTES : "skriver"
    USERS ||--o{ PASSWORD_RESET_TOKENS : "har"
    SUGGESTIONS |o--o{ TRIP_DESTINATIONS : "ligger till grund för"
    SUGGESTIONS |o--o{ ACTIVITIES : "ligger till grund för"
 
    USERS {
        bigint id PK
        varchar name "100"
        varchar email UK "255"
        varchar password_hash "255"
        varchar profile_image "255, NULL"
        datetime last_login "NULL"
        datetime created_at
        datetime updated_at
    }
    TRIPS {
        bigint id PK
        bigint owner_id FK
        varchar title "150"
        date start_date
        date end_date "CK: end_date >= start_date"
        datetime created_at
        datetime updated_at
    }
    TRIP_MEMBERS {
        bigint trip_id PK, FK
        bigint user_id PK, FK
        enum role "EDITOR | VIEWER"
        datetime joined_at
        datetime updated_at
    }
    INVITATIONS {
        bigint id PK
        bigint trip_id FK "UK tillsammans med invitee_id"
        bigint inviter_id FK
        bigint invitee_id FK "UK tillsammans med trip_id"
        enum status
        datetime created_at
        datetime responded_at "NULL"
    }
    TRIP_DESTINATIONS {
        bigint id PK
        bigint trip_id FK "UK tillsammans med order_index"
        bigint source_suggestion_id FK "NULL"
        varchar country "100"
        varchar place_name "150"
        int order_index "UK tillsammans med trip_id"
        datetime created_at
    }
    ACTIVITIES {
        bigint id PK
        bigint trip_destination_id FK
        bigint created_by FK "NULL"
        bigint source_suggestion_id FK "NULL"
        varchar name "150"
        date activity_date
        time start_time
        text description "NULL"
        enum status
        datetime created_at
        datetime updated_at
    }
    ACTIVITY_VOTES {
        bigint activity_id PK, FK
        bigint user_id PK, FK
        datetime created_at
    }
    NOTES {
        bigint id PK
        bigint trip_id FK
        bigint author_id FK
        text content
        datetime created_at
        datetime updated_at
    }
    PASSWORD_RESET_TOKENS {
        bigint id PK
        bigint user_id FK
        char token_hash UK "64"
        datetime expires_at
        datetime used_at "NULL"
        datetime created_at
    }
    SUGGESTIONS {
        bigint id PK
        enum type "COUNTRY | CITY | ACTIVITY"
        varchar name "150"
        text description "NULL"
        varchar country "100, NULL"
        varchar city "100, NULL"
        varchar image_url "255, NULL"
    }
```
 
## Tabeller
 
| Tabell | Innehåll |
|---|---|
| `users` | Ett användarkonto med namn, unik e-postadress, hashat lösenord, valfri profilbild och senaste inloggning. |
| `trips` | En resa med titel och datumintervall. Varje resa har en ägare (`owner_id`). Ett villkor säkerställer att `end_date` inte ligger före `start_date`. |
| `trip_members` | Kopplingstabell mellan användare och resor (många till många). `role` anger om medlemmen är `EDITOR` eller `VIEWER`. Ägaren och accepterade inbjudna finns här. |
| `invitations` | En inbjudan från en användare (`inviter_id`) till en annan (`invitee_id`) att gå med i en resa. En användare kan bara bjudas in en gång per resa. När inbjudan har status ACCEPTED skapas en rad i `trip_members`. |
| `trip_destinations` | En destination (land och plats) i en resa. `order_index` bestämmer ordningen och är unik per resa. Kan valfritt komma från ett förslag. |
| `activities` | En aktivitet på en destination, med datum, starttid, beskrivning och status. Kan valfritt komma från ett förslag och har en skapare (`created_by`). |
| `activity_votes` | En användares röst på en aktivitet. Den sammansatta primärnyckeln (`activity_id` + `user_id`) ger en röst per användare och aktivitet. |
| `notes` | Anteckningar som hör till en resa, med författare och innehåll. |
| `password_reset_tokens` | Engångstoken för återställning av lösenord. Bara hashen sparas. Token har utgångstid och markeras som använd i `used_at`. |
| `suggestions` | Färdiga förslag på länder, städer och aktiviteter (`type`). Fristående tabell som destinationer och aktiviteter valfritt kan kopplas till. |
 
## Relationer
 
1. En användare kan skapa flera resor (`trips.owner_id`).
2. En resa kan ha flera destinationer, aktiviteter (via destinationerna) och anteckningar.
3. Flera användare kan delta i samma resa via `trip_members`.
4. En inbjuden blir medlem först när inbjudan accepteras.
5. Varje användare kan rösta en gång per aktivitet.
6. Kopplingen till `suggestions` är valfri (streckad linje i ERD:n) och kan vara NULL.
## Databasregler och antaganden
 
- **CASCADE:** Tas en resa bort raderas dess destinationer, medlemmar, inbjudningar, anteckningar och röster. Tas en användare bort raderas dennes `password_reset_tokens`.
- **SET NULL:** `activities.created_by` och båda `source_suggestion_id` sätts till NULL om raden de pekar på tas bort.
- **Backendregler:** Endast medlemmar i en resa får rösta. `activity_date` måste ligga inom resans datum. Detta kan inte uttryckas som kontrollvillkor i databasen och kontrolleras därför i backend.
- **Autentisering:** Auth antas vara stateless (JWT).
