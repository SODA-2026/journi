-- 1. USERS
INSERT INTO users (id, name, email, password_hash, profile_image, last_login) VALUES
                                                                                  (1, 'Anna Andersson', 'anna.andersson@example.com', '$2a$12$eImiTXuWVxfM37uY4JANjOL.81F8Rkhv/1.8R.gq3w3g.2h2/q9i.', '/images/profiles/anna.jpg', '2027-06-01 10:00:00'),
                                                                                  (2, 'Björn Nygård', 'bjorn.nygard@example.com', '$2a$12$eImiTXuWVxfM37uY4JANjOL.81F8Rkhv/1.8R.gq3w3g.2h2/q9i.', '/images/profiles/bjorn.jpg', '2027-06-02 11:30:00'),
                                                                                  (3, 'Clara Jensen', 'clara.jensen@example.com', '$2a$12$eImiTXuWVxfM37uY4JANjOL.81F8Rkhv/1.8R.gq3w3g.2h2/q9i.', NULL, '2027-06-03 14:15:00');

-- 2. TRIPS
INSERT INTO trips (id, owner_id, title, start_date, end_date) VALUES
    (1, 1, 'Skandinavisk Sommarresa 2027', '2027-07-01', '2027-07-15');

-- 3. INVITATIONS
INSERT INTO invitations (id, trip_id, inviter_id, invitee_id, status, responded_at) VALUES
                                                                                        (1, 1, 1, 2, 'ACCEPTED', '2027-06-05 12:00:00'),
                                                                                        (2, 1, 1, 3, 'PENDING', NULL);

-- 4. TRIP_MEMBERS
INSERT INTO trip_members (trip_id, user_id, role, joined_at) VALUES
    (1, 2, 'EDITOR', '2027-06-05 12:00:00');

-- 5. SUGGESTIONS (Countries, Cities, Activities)
-- Countries
INSERT INTO suggestions (id, type, destination_kind, parent_suggestion_id, name, country, region, category, short_description, description, best_travel_time, image_url) VALUES
                                                                                                                                                                             (1, 'DESTINATION', 'COUNTRY', NULL, 'Sverige', 'Sverige', NULL, NULL, 'Upplev fantastisk natur, skogar och vacker skärgård.', 'Sverige erbjuder fantastiska möjligheter för både stads- och naturupplevelser.', 'Juni-Augusti', '/images/destinations/sweden.jpg'),
                                                                                                                                                                             (2, 'DESTINATION', 'COUNTRY', NULL, 'Norge', 'Norge', NULL, NULL, 'Dramatiska fjordar, höga berg och vild natur.', 'Norge är känt för sin storslagna natur och sina historiska städer.', 'Maj-September', '/images/destinations/norway.jpg'),
                                                                                                                                                                             (3, 'DESTINATION', 'COUNTRY', NULL, 'Danmark', 'Danmark', NULL, NULL, 'Känt för sin design, cykelkultur och mysiga hygge.', 'Danmark bjuder på charmiga kuster och världsledande arkitektur.', 'Maj-Augusti', '/images/destinations/denmark.jpg'),

-- Cities (Linked to Countries)
                                                                                                                                                                             (4, 'DESTINATION', 'CITY', 1, 'Malmö', 'Sverige', 'Skåne', NULL, 'Mångkulturell kuststad känd för Turning Torso och lummiga parker.', 'Skånes största stad med ett rikt kulturliv och närhet till havet.', 'Maj-September', '/images/destinations/malmo.jpg'),
                                                                                                                                                                             (5, 'DESTINATION', 'CITY', 2, 'Oslo', 'Norge', 'Oslo Fjord', NULL, 'Norges huvudstad vackert belägen vid Oslofjorden.', 'En kombination av modern arkitektur och rik historia.', 'Maj-Augusti', '/images/destinations/oslo.jpg'),
                                                                                                                                                                             (6, 'DESTINATION', 'CITY', 3, 'Köpenhamn', 'Danmark', 'Hovedstaden', NULL, 'Danmarks pulserande huvudstad fylld med vacker arkitektur.', 'Känd för Nyhavn, cykelstråk och sin kulinariska scen.', 'Maj-September', '/images/destinations/copenhagen.jpg'),

-- Activities (Linked to Cities)
                                                                                                                                                                             (7, 'ACTIVITY', NULL, 4, 'Besök Turning Torso', NULL, NULL, 'Sevärdhet', 'Beundra Nordens högsta skyskrapa och landmärket i Västra Hamnen.', 'En arkitektonisk pärla designad av Santiago Calatrava.', NULL, '/images/activities/turning-torso.jpg'),
                                                                                                                                                                             (8, 'ACTIVITY', NULL, 5, 'Operahuset i Oslo', NULL, NULL, 'Kultur', 'Promenera på det snövita taket av Oslos moderna operahus.', 'Upplev spektakulär utsikt över Oslofjorden från operahusets tak.', NULL, '/images/activities/oslo-opera.jpg'),
                                                                                                                                                                             (9, 'ACTIVITY', NULL, 6, 'Tivoli Köpenhamn', NULL, NULL, 'Nöje', 'Klassisk och magisk nöjespark mitt i hjärtat av Köpenhamn.', 'En av världens äldsta nöjesparker med vackra trädgårdar och attraktioner.', NULL, '/images/activities/tivoli.jpg');

-- 6. TRIP_DESTINATIONS
INSERT INTO trip_destinations (id, trip_id, source_suggestion_id, country, place_name, order_index) VALUES
                                                                                                        (1, 1, 4, 'Sverige', 'Malmö', 1),
                                                                                                        (2, 1, 5, 'Norge', 'Oslo', 2),
                                                                                                        (3, 1, 6, 'Danmark', 'Köpenhamn', 3);

-- 7. ACTIVITIES
INSERT INTO activities (id, trip_destination_id, created_by, source_suggestion_id, name, activity_date, start_time, description, status) VALUES
                                                                                                                                             (1, 1, 1, 7, 'Besök Turning Torso', '2027-07-02', '14:00:00', 'Promenera runt i Västra Hamnen och fota tornet.', 'PLANNED'),
                                                                                                                                             (2, 2, 2, 8, 'Operahuset i Oslo', '2027-07-06', '11:00:00', 'Gå upp på taket och ta fikapaus.', 'PLANNED'),
                                                                                                                                             (3, 3, 1, 9, 'Tivoli Köpenhamn', NULL, NULL, 'Ska vi gå på kvällen eller eftermiddagen?', 'PROPOSED');

-- 8. ACTIVITY_VOTES
INSERT INTO activity_votes (activity_id, user_id) VALUES
                                                      (3, 1),
                                                      (3, 2);

-- 9. NOTES
INSERT INTO notes (id, trip_id, author_id, content) VALUES
                                                        (1, 1, 1, 'Kom ihåg att boka tågbiljetter mellan Malmö och Köpenhamn i god tid!'),
                                                        (2, 1, 2, 'Vi borde kolla upp parkeringsmöjligheter nära hotellet i Oslo.');

-- 10. PASSWORD_RESET_TOKENS
INSERT INTO password_reset_tokens (id, user_id, token_hash, expires_at) VALUES
    (1, 3, 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', '2027-06-01 12:00:00');