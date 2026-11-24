-- Demo-data för Journi. Körs mot en databas där migreringarna V1-V6 redan är körda.
-- Exempel: mysql -u <användare> -p <databas> < backend/scripts/demo-data.sql
-- Inloggning: test@example.com / password123 (samma lösenord för alla användare)
-- OBS: raderar ALLA användare och resor i databasen. Länder, städer och platser (V6) rörs inte.

SET NAMES utf8mb4;


-- Rensa gammal data
DELETE FROM trips WHERE id > 0;
DELETE FROM users WHERE id > 0;


-- Städer och platser (id hämtas via namn, de kommer från V6)
SET @stockholm := (SELECT id FROM cities WHERE name = 'Stockholm');
SET @goteborg  := (SELECT id FROM cities WHERE name = 'Göteborg');
SET @malmo     := (SELECT id FROM cities WHERE name = 'Malmö');
SET @oslo      := (SELECT id FROM cities WHERE name = 'Oslo');
SET @bergen    := (SELECT id FROM cities WHERE name = 'Bergen');
SET @kopenhamn := (SELECT id FROM cities WHERE name = 'Köpenhamn');

SET @gamla_stan      := (SELECT id FROM places WHERE name = 'Gamla stan'      AND city_id = @stockholm);
SET @vasamuseet      := (SELECT id FROM places WHERE name = 'Vasamuseet'      AND city_id = @stockholm);
SET @liseberg        := (SELECT id FROM places WHERE name = 'Liseberg'        AND city_id = @goteborg);
SET @haga            := (SELECT id FROM places WHERE name = 'Haga'            AND city_id = @goteborg);
SET @feskekorka      := (SELECT id FROM places WHERE name = 'Feskekôrka'      AND city_id = @goteborg);
SET @turning_torso   := (SELECT id FROM places WHERE name = 'Turning Torso'   AND city_id = @malmo);
SET @lilla_torg      := (SELECT id FROM places WHERE name = 'Lilla torg'      AND city_id = @malmo);
SET @operahuset      := (SELECT id FROM places WHERE name = 'Operahuset'      AND city_id = @oslo);
SET @vigelandsparken := (SELECT id FROM places WHERE name = 'Vigelandsparken' AND city_id = @oslo);
SET @holmenkollen    := (SELECT id FROM places WHERE name = 'Holmenkollen'    AND city_id = @oslo);
SET @aker_brygge     := (SELECT id FROM places WHERE name = 'Aker Brygge'     AND city_id = @oslo);
SET @bryggen         := (SELECT id FROM places WHERE name = 'Bryggen'         AND city_id = @bergen);
SET @floibanen       := (SELECT id FROM places WHERE name = 'Fløibanen'       AND city_id = @bergen);
SET @fisktorget      := (SELECT id FROM places WHERE name = 'Fisktorget'      AND city_id = @bergen);
SET @tivoli          := (SELECT id FROM places WHERE name = 'Tivoli'          AND city_id = @kopenhamn);
SET @nyhavn          := (SELECT id FROM places WHERE name = 'Nyhavn'          AND city_id = @kopenhamn);
SET @rundetarn       := (SELECT id FROM places WHERE name = 'Rundetårn (Runda tornet)' AND city_id = @kopenhamn);


-- Användare
INSERT INTO users (id, name, email, password_hash) VALUES
    (1, 'Test Testsson',   'test@example.com',  '$2a$10$M19vNiOiZgbxahWpBC1sS.F80Ccrf5RjNrDvxsqk2NE6Cky6UTQgC'),
    (2, 'Anna Andersson',  'anna@example.com',  '$2a$10$M19vNiOiZgbxahWpBC1sS.F80Ccrf5RjNrDvxsqk2NE6Cky6UTQgC'),
    (3, 'Björn Nygård',    'bjorn@example.com', '$2a$10$M19vNiOiZgbxahWpBC1sS.F80Ccrf5RjNrDvxsqk2NE6Cky6UTQgC'),
    (4, 'Clara Jensen',    'clara@example.com', '$2a$10$M19vNiOiZgbxahWpBC1sS.F80Ccrf5RjNrDvxsqk2NE6Cky6UTQgC'),
    (5, 'David Lindqvist', 'david@example.com', '$2a$10$M19vNiOiZgbxahWpBC1sS.F80Ccrf5RjNrDvxsqk2NE6Cky6UTQgC'),
    (6, 'Elin Strand',     'elin@example.com',  '$2a$10$M19vNiOiZgbxahWpBC1sS.F80Ccrf5RjNrDvxsqk2NE6Cky6UTQgC');


-- Resor (datum räknas från idag)
--   1 Skandinavisk Rundresa    ägare Test   tre städer, huvudresan i demon
--   2 Weekend i Göteborg       ägare Anna   Test är medlem
--   3 Stockholm på egen hand   ägare Test   tom resa
--   4 Norgeresa                ägare Björn  Test har en väntande inbjudan
--   5 Kollegaresa till Köpenhamn ägare Test fyra inbjudna
--   6 Stockholmshelg           ägare Test   tidigare resa
SET @today := CURDATE();
SET @t1 := @today + INTERVAL 30 DAY;
SET @t2 := @today + INTERVAL 10 DAY;
SET @t4 := @today + INTERVAL 60 DAY;
SET @t5 := @today + INTERVAL 120 DAY;
SET @t6 := @today - INTERVAL 60 DAY;

INSERT INTO trips (id, owner_id, title, start_date, end_date) VALUES
    (1, 1, 'Skandinavisk Rundresa',        @t1, @t1 + INTERVAL 14 DAY),
    (2, 2, 'Weekend i Göteborg',           @t2, @t2 + INTERVAL 2 DAY),
    (3, 1, 'Stockholm på egen hand',       @today + INTERVAL 90 DAY, @today + INTERVAL 94 DAY),
    (4, 3, 'Norgeresa',                    @t4, @t4 + INTERVAL 7 DAY),
    (5, 1, 'Kollegaresa till Köpenhamn',   @t5, @t5 + INTERVAL 3 DAY),
    (6, 1, 'Stockholmshelg',               @t6, @t6 + INTERVAL 2 DAY);


-- Inbjudningar
INSERT INTO invitations (id, trip_id, inviter_id, invitee_id, status, responded_at) VALUES
    (1,  1, 1, 2, 'ACCEPTED', NOW() - INTERVAL 5 DAY),
    (2,  1, 1, 3, 'PENDING',  NULL),
    (3,  1, 1, 4, 'DECLINED', NOW() - INTERVAL 4 DAY),
    (4,  1, 1, 5, 'ACCEPTED', NOW() - INTERVAL 3 DAY),
    (5,  2, 2, 1, 'ACCEPTED', NOW() - INTERVAL 7 DAY),
    (6,  4, 3, 1, 'PENDING',  NULL),
    (7,  4, 3, 2, 'ACCEPTED', NOW() - INTERVAL 2 DAY),
    (8,  5, 1, 2, 'ACCEPTED', NOW() - INTERVAL 1 DAY),
    (9,  5, 1, 5, 'ACCEPTED', NOW() - INTERVAL 1 DAY),
    (10, 5, 1, 6, 'PENDING',  NULL),
    (11, 5, 1, 4, 'DECLINED', NOW() - INTERVAL 1 DAY),
    (12, 6, 1, 2, 'ACCEPTED', NOW() - INTERVAL 70 DAY);


-- Medlemmar (de som tackat ja, ägaren har ingen rad här)
INSERT INTO trip_members (trip_id, user_id) VALUES
    (1, 2), (1, 5),
    (2, 1),
    (4, 2),
    (5, 2), (5, 5),
    (6, 2);


-- Destinationer
-- order_index är 0-baserat, samma konvention som backend (TripService börjar på 0).
INSERT INTO trip_destinations (id, trip_id, city_id, order_index) VALUES
    (1, 1, @malmo,     0),
    (2, 1, @oslo,      1),
    (3, 1, @kopenhamn, 2),
    (4, 2, @goteborg,  0),
    (5, 3, @stockholm, 0),
    (6, 4, @bergen,    0),
    (7, 4, @oslo,      1),
    (8, 5, @kopenhamn, 0),
    (9, 6, @stockholm, 0);


-- Aktiviteter (place_id NULL = egen aktivitet utan plats)
INSERT INTO trip_activities (id, trip_destination_id, place_id, name, activity_date, start_time, description, status) VALUES
    -- Malmö
    (1,  1, @turning_torso,   'Besök Turning Torso',      @t1 + INTERVAL 1 DAY,  '14:00:00', 'Promenera runt i Västra hamnen och fota tornet.', 'PLANNED'),
    (2,  1, @lilla_torg,      'Fika på Lilla torg',       NULL,                  NULL,       'Mysig plats med uteserveringar.',                 'PROPOSED'),
    (3,  1, NULL,             'Middag i Malmö',           NULL,                  NULL,       'Restaurang ej bokad än.',                         'PROPOSED'),
    -- Oslo
    (4,  2, @operahuset,      'Operahuset i Oslo',        @t1 + INTERVAL 5 DAY,  '11:00:00', 'Gå upp på taket och ta fikapaus.',                'PLANNED'),
    (5,  2, @vigelandsparken, 'Vigelandsparken',          NULL,                  NULL,       'Passar bra en lugn förmiddag.',                   'PROPOSED'),
    (6,  2, @holmenkollen,    'Hoppbacken Holmenkollen',  @t1 + INTERVAL 6 DAY,  '13:00:00', 'Utsikt över hela Oslo.',                          'PLANNED'),
    -- Köpenhamn (huvudresan)
    (7,  3, @tivoli,          'Tivoli Köpenhamn',         NULL,                  NULL,       'Kväll eller eftermiddag?',                        'PROPOSED'),
    (8,  3, @nyhavn,          'Middag vid Nyhavn',        @t1 + INTERVAL 9 DAY,  '18:30:00', NULL,                                              'PLANNED'),
    (9,  3, NULL,             'Cykeltur i staden',        @t1 + INTERVAL 10 DAY, '10:00:00', 'Inställd på grund av dåligt väder.',              'CANCELLED'),
    -- Göteborg
    (10, 4, @liseberg,        'Liseberg',                 @t2 + INTERVAL 1 DAY,  '12:00:00', NULL,                                              'PLANNED'),
    (11, 4, @feskekorka,      'Lunch i Feskekôrka',       NULL,                  NULL,       NULL,                                              'PROPOSED'),
    (12, 4, @haga,            'Frukost i Haga',           @t2 + INTERVAL 2 DAY,  '10:30:00', 'Kanelbullar i Hagas gamla trähus.',               'PLANNED'),
    -- Bergen
    (13, 6, @floibanen,       'Fløibanen',                @t4 + INTERVAL 1 DAY,  '10:00:00', 'Åk upp för utsikten över staden.',                'PLANNED'),
    (14, 6, @bryggen,         'Promenad på Bryggen',      @t4 + INTERVAL 1 DAY,  '14:00:00', NULL,                                              'PLANNED'),
    (15, 6, @fisktorget,      'Lunch på Fisktorget',      NULL,                  NULL,       'Färsk fisk och skaldjur.',                        'PROPOSED'),
    -- Oslo (Norgeresan)
    (16, 7, @aker_brygge,     'Middag på Aker Brygge',    @t4 + INTERVAL 5 DAY,  '19:00:00', NULL,                                              'PLANNED'),
    -- Köpenhamn (kollegaresan)
    (17, 8, @nyhavn,          'After work vid Nyhavn',    @t5 + INTERVAL 1 DAY,  '17:00:00', NULL,                                              'PLANNED'),
    (18, 8, @tivoli,          'Tivoli på kvällen',        @t5 + INTERVAL 2 DAY,  '19:00:00', NULL,                                              'PLANNED'),
    (19, 8, @rundetarn,       'Runda tornet',             NULL,                  NULL,       'Kort stopp om vi hinner.',                        'PROPOSED'),
    -- Stockholmshelgen (tidigare resa)
    (20, 9, @vasamuseet,      'Vasamuseet',               @t6,                   '11:00:00', NULL,                                              'PLANNED'),
    (21, 9, @gamla_stan,      'Fika i Gamla stan',        @t6 + INTERVAL 1 DAY,  '13:00:00', NULL,                                              'PLANNED');


-- Anteckningar
INSERT INTO trip_notes (id, trip_id, author_id, content) VALUES
    (1, 1, 1, 'Kom ihåg att boka tågbiljetter mellan Malmö och Köpenhamn i god tid!'),
    (2, 1, 2, 'Vi borde kolla upp parkeringsmöjligheter nära hotellet i Oslo.'),
    (3, 1, 5, 'Jag kan ta med kameran och fota allt på resan.'),
    (4, 2, 2, 'Hotellet ligger nära Centralstationen, incheckning efter 15:00.'),
    (5, 4, 3, 'Boka Fløibanen i förväg, det blir fullt på helgerna.'),
    (6, 5, 1, 'Mötet slutar kl. 16, så vi hinner med middag samma dag.'),
    (7, 6, 2, 'Det var en jättefin helg, vi gör om det!');
