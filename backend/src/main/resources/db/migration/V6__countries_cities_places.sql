-- V6: Seed-data för länder, städer och platser (Sverige, Norge, Danmark).
-- Tio städer per land och tio platser per stad.
-- Städernas och ländernas id hämtas med LAST_INSERT_ID(), så inga id är hårdkodade.

-- =====================================================
-- SVERIGE
-- =====================================================
INSERT INTO countries (name) VALUES ('Sverige');
SET @country_id := LAST_INSERT_ID();

-- Stockholm
INSERT INTO cities (name, country_id) VALUES ('Stockholm', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Gamla stan', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Vasamuseet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Skansen', @city_id, 'FAMILY'),
                                                 ('ABBA The Museum', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Djurgården', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Kungliga slottet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Stadshuset', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Fotografiska', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Moderna museet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Drottningholms slott', @city_id, 'TOURIST_ATTRACTION');

-- Göteborg
INSERT INTO cities (name, country_id) VALUES ('Göteborg', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Liseberg', @city_id, 'FAMILY'),
                                                 ('Haga', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Slottsskogen', @city_id, 'FAMILY'),
                                                 ('Universeum', @city_id, 'FAMILY'),
                                                 ('Göteborgs konstmuseum', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Feskekôrka', @city_id, 'FOOD'),
                                                 ('Avenyn', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Älvsborgs fästning', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Botaniska trädgården', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Södra skärgården (t.ex. Styrsö)', @city_id, 'TOURIST_ATTRACTION');

-- Malmö
INSERT INTO cities (name, country_id) VALUES ('Malmö', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Turning Torso', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Malmöhus slott', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Stortorget', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Lilla torg', @city_id, 'BAR'),
                                                 ('Ribersborgs kallbadhus', @city_id, 'SPORT'),
                                                 ('Västra hamnen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Moderna Museet Malmö', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Folkets park', @city_id, 'FAMILY'),
                                                 ('Slottsparken', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Malmö Saluhall', @city_id, 'FOOD');

-- Uppsala
INSERT INTO cities (name, country_id) VALUES ('Uppsala', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Uppsala domkyrka', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Uppsala slott', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Gamla Uppsala', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Carolina Rediviva', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Linnéträdgården', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Linnés Hammarby', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Fyrisån', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Botaniska trädgården', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Upplandsmuseet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Gustavianum', @city_id, 'TOURIST_ATTRACTION');

-- Visby
INSERT INTO cities (name, country_id) VALUES ('Visby', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Ringmuren', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Sankta Maria domkyrka', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('S:t Karins ruin', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Botaniska trädgården', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Gotlands Museum', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Almedalen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Kruttornet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Galgberget', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Hamnen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Stora torget', @city_id, 'TOURIST_ATTRACTION');

-- Lund
INSERT INTO cities (name, country_id) VALUES ('Lund', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Lunds domkyrka', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Kulturen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Lundagård', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Botaniska trädgården', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Skissernas museum', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Stadsparken', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Historiska museet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Mejeriet', @city_id, 'BAR'),
                                                 ('Lunds universitet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Observatoriet', @city_id, 'TOURIST_ATTRACTION');

-- Kiruna
INSERT INTO cities (name, country_id) VALUES ('Kiruna', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Kiruna kyrka', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Stadshuset', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Gruvbesök (LKAB)', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Hjalmar Lundbohmsgården', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Luossavaara', @city_id, 'SPORT'),
                                                 ('Jukkasjärvi Ishotell', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Abisko nationalpark', @city_id, 'SPORT'),
                                                 ('Aurora Sky Station', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Kebnekaise', @city_id, 'SPORT'),
                                                 ('Torneträsk', @city_id, 'TOURIST_ATTRACTION');

-- Umeå
INSERT INTO cities (name, country_id) VALUES ('Umeå', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Bildmuseet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Gammlia friluftsmuseum', @city_id, 'FAMILY'),
                                                 ('Guitars – The Museum', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Väven', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Norrlandsoperan', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Umeälven', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Umedalen Skulptur', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Hamnmagasinet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Nydalasjön', @city_id, 'SPORT'),
                                                 ('Holmön', @city_id, 'TOURIST_ATTRACTION');

-- Örebro
INSERT INTO cities (name, country_id) VALUES ('Örebro', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Örebro slott', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Svampen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Wadköping', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Stadsparken', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Engelbrektsstatyn', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Rådhuset', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Örebro konsthall', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Hjälmaren', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Länsmuseet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Karlslund herrgård', @city_id, 'TOURIST_ATTRACTION');

-- Helsingborg
INSERT INTO cities (name, country_id) VALUES ('Helsingborg', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Kärnan', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Sofiero slott', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Fredriksdal', @city_id, 'FAMILY'),
                                                 ('Dunkers kulturhus', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Mariakyrkan', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Terrassen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Hamnen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Pålsjö skog', @city_id, 'SPORT'),
                                                 ('Rådhuset', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Färjan till Helsingör', @city_id, 'TOURIST_ATTRACTION');

-- =====================================================
-- NORGE
-- =====================================================
INSERT INTO countries (name) VALUES ('Norge');
SET @country_id := LAST_INSERT_ID();

-- Oslo
INSERT INTO cities (name, country_id) VALUES ('Oslo', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Operahuset', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Vigelandsparken', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Vikingaskeppsmuseet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Akershus fästning', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Holmenkollen', @city_id, 'SPORT'),
                                                 ('Munchmuseet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Frammuseet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Nobels fredscenter', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Aker Brygge', @city_id, 'FOOD'),
                                                 ('Nationalmuseet', @city_id, 'TOURIST_ATTRACTION');

-- Bergen
INSERT INTO cities (name, country_id) VALUES ('Bergen', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Bryggen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Fløibanen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Fisktorget', @city_id, 'FOOD'),
                                                 ('Ulriken', @city_id, 'SPORT'),
                                                 ('Bergenhus fästning', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Troldhaugen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('KODE', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Gamla Bergens museum', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Akvariet i Bergen', @city_id, 'FAMILY'),
                                                 ('Nordnes', @city_id, 'TOURIST_ATTRACTION');

-- Trondheim
INSERT INTO cities (name, country_id) VALUES ('Trondheim', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Nidarosdomen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Gamla stadsbron', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Bakklandet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Kristiansten fästning', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Ärkebiskopsgården', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Ravnkloa', @city_id, 'FOOD'),
                                                 ('Sverresborgs folkmuseum', @city_id, 'FAMILY'),
                                                 ('Rockheim', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Munkholmen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Tyholttornet', @city_id, 'TOURIST_ATTRACTION');

-- Stavanger
INSERT INTO cities (name, country_id) VALUES ('Stavanger', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Preikestolen', @city_id, 'SPORT'),
                                                 ('Gamla Stavanger', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Norska oljemuseet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Lysefjorden', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Stavanger domkyrka', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Kjerag', @city_id, 'SPORT'),
                                                 ('Svärd i berg', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Breiavatnet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Utstein kloster', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Norska konservmuseet', @city_id, 'TOURIST_ATTRACTION');

-- Tromsö
INSERT INTO cities (name, country_id) VALUES ('Tromsö', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Ishavskatedralen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Fjellheisen (bergbanan)', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Polaria', @city_id, 'FAMILY'),
                                                 ('Polarmuseet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Tromsöbron', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Norrskensturer', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Sommarøy', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Telegrafbukta', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Kvaløya', @city_id, 'SPORT'),
                                                 ('Tromsö botaniska trädgård', @city_id, 'TOURIST_ATTRACTION');

-- Ålesund
INSERT INTO cities (name, country_id) VALUES ('Ålesund', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Jugendstilcentret', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Aksla (Fjellstua)', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Atlanterhavsparken', @city_id, 'FAMILY'),
                                                 ('Alnes fyr', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Giske', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Runde fågelberg', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Geirangerfjorden', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Sunnmöre museum', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Brosundet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Hjørundfjorden', @city_id, 'TOURIST_ATTRACTION');

-- Kristiansand
INSERT INTO cities (name, country_id) VALUES ('Kristiansand', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Djurparken', @city_id, 'FAMILY'),
                                                 ('Posebyen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Fiskebryggan', @city_id, 'FOOD'),
                                                 ('Odderøya', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Kunstsilo', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Christiansholms fästning', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Ravnedalen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Baneheia', @city_id, 'SPORT'),
                                                 ('Gimle gård', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Markens gata', @city_id, 'TOURIST_ATTRACTION');

-- Bodö
INSERT INTO cities (name, country_id) VALUES ('Bodö', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Saltstraumen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Norska flygmuseet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Kjerringøy handelsplats', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Svartisen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Stormen kulturhus', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Bodins kyrka', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Keiservarden', @city_id, 'SPORT'),
                                                 ('Bodö domkyrka', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Aspmyra stadion', @city_id, 'SPORT'),
                                                 ('Hurtigrutenkajen', @city_id, 'TOURIST_ATTRACTION');

-- Lillehammer
INSERT INTO cities (name, country_id) VALUES ('Lillehammer', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Maihaugen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Lysgårdsbackarna', @city_id, 'SPORT'),
                                                 ('Olympiaparken', @city_id, 'SPORT'),
                                                 ('Hunderfossen familjepark', @city_id, 'FAMILY'),
                                                 ('Lillehammers konstmuseum', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Nordseter', @city_id, 'SPORT'),
                                                 ('Mjösa', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Skibladner', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Hafjell', @city_id, 'SPORT'),
                                                 ('Gågatan', @city_id, 'TOURIST_ATTRACTION');

-- Alta
INSERT INTO cities (name, country_id) VALUES ('Alta', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Hällristningarna i Alta (UNESCO)', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Altamuseet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Norrskenskatedralen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Altaelva', @city_id, 'SPORT'),
                                                 ('Finnmarksløpet', @city_id, 'SPORT'),
                                                 ('Altacanyon', @city_id, 'SPORT'),
                                                 ('Sorrisniva Igloo Hotel', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Alta kyrka', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Komsa', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Altafjorden', @city_id, 'TOURIST_ATTRACTION');

-- =====================================================
-- DANMARK
-- =====================================================
INSERT INTO countries (name) VALUES ('Danmark');
SET @country_id := LAST_INSERT_ID();

-- Köpenhamn
INSERT INTO cities (name, country_id) VALUES ('Köpenhamn', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Tivoli', @city_id, 'FAMILY'),
                                                 ('Nyhavn', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Lilla sjöjungfrun', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Amalienborg', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Rosenborg slott', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Christiansborg', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Strøget', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Christiania', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Rundetårn (Runda tornet)', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Nationalmuseet', @city_id, 'TOURIST_ATTRACTION');

-- Århus
INSERT INTO cities (name, country_id) VALUES ('Århus', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Den Gamle By (Gamla staden)', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('ARoS', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Moesgaard Museum', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Latinerkvarteret (Latinkvarteret)', @city_id, 'BAR'),
                                                 ('Tivoli Friheden', @city_id, 'FAMILY'),
                                                 ('Århus domkyrka', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Marselisborg', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Dokk1', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Botaniska trädgården', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Infinite Bridge', @city_id, 'TOURIST_ATTRACTION');

-- Odense
INSERT INTO cities (name, country_id) VALUES ('Odense', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('H.C. Andersens hus', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Fynske byn', @city_id, 'FAMILY'),
                                                 ('Odense djurpark', @city_id, 'FAMILY'),
                                                 ('Brandts', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Møntergården', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Sankt Knuds kyrka', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Munke Mølle', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Fyns konstmuseum', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Carl Nielsen-museet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Egeskov slott (i närheten)', @city_id, 'TOURIST_ATTRACTION');

-- Ålborg
INSERT INTO cities (name, country_id) VALUES ('Ålborg', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Jomfru Ane Gade (Jomfru Ane-gatan)', @city_id, 'BAR'),
                                                 ('Ålborg Zoo', @city_id, 'FAMILY'),
                                                 ('Utzon Center', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Kunsten', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Ålborgtornet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Nordkraft', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Budolfi kyrka', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Ålborgs slott', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Lindholm Høje', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Jens Bangs stenhus', @city_id, 'TOURIST_ATTRACTION');

-- Esbjerg
INSERT INTO cities (name, country_id) VALUES ('Esbjerg', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Fanø', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Människan vid havet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Fiskeri- och sjöfartsmuseet', @city_id, 'FAMILY'),
                                                 ('Vattentornet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Esbjergs konstmuseum', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Vadehavscentret', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Mandø', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Marbæk plantage', @city_id, 'SPORT'),
                                                 ('Sønderho', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Esbjerg hamn', @city_id, 'TOURIST_ATTRACTION');

-- Roskilde
INSERT INTO cities (name, country_id) VALUES ('Roskilde', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Roskilde domkyrka', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Vikingaskeppsmuseet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Roskilde Festival', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Ragnarock', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Museet för samtidskonst', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Land of Legends (Lejre)', @city_id, 'FAMILY'),
                                                 ('Roskildefjorden', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Sankt Jørgensbjerg', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Kongehøj', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Gågatorna', @city_id, 'TOURIST_ATTRACTION');

-- Helsingör
INSERT INTO cities (name, country_id) VALUES ('Helsingör', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Kronborg', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('M/S Sjöfartsmuseet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Sankt Olai kyrka', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Karmeliterklostret', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Kulturværftet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Marienlyst strand', @city_id, 'SPORT'),
                                                 ('Gamla stan', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Fredensborg slott', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Gilleleje', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Louisiana (Humlebæk)', @city_id, 'TOURIST_ATTRACTION');

-- Ribe
INSERT INTO cities (name, country_id) VALUES ('Ribe', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Ribe domkyrka', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Ribe Vikingecenter', @city_id, 'FAMILY'),
                                                 ('Ribe konstmuseum', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Ribes gamla stad', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Sankta Katarina kyrka och kloster', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Vadehavscentret', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Mandø', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Ribe å', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Ribes vikingamuseum', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Slusen', @city_id, 'TOURIST_ATTRACTION');

-- Billund
INSERT INTO cities (name, country_id) VALUES ('Billund', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Legoland', @city_id, 'FAMILY'),
                                                 ('LEGO House', @city_id, 'FAMILY'),
                                                 ('Lalandia', @city_id, 'FAMILY'),
                                                 ('Givskud Zoo', @city_id, 'FAMILY'),
                                                 ('Jelling (UNESCO)', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Egtved', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Vejlefjorden', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Billunds flygplatsutställning', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Lego Inside Tour', @city_id, 'FAMILY'),
                                                 ('Frederiks Hus', @city_id, 'TOURIST_ATTRACTION');

-- Skagen
INSERT INTO cities (name, country_id) VALUES ('Skagen', @country_id);
SET @city_id := LAST_INSERT_ID();
INSERT INTO places (name, city_id, category) VALUES
                                                 ('Grenen', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Råbjerg Mile', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Skagens museum', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Den igensandade kyrkan', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Skagens fyr', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Skagens hamn', @city_id, 'FOOD'),
                                                 ('Anchers hus', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Skagen Odde naturcenter', @city_id, 'FAMILY'),
                                                 ('Bunkermuseet', @city_id, 'TOURIST_ATTRACTION'),
                                                 ('Skagens strand', @city_id, 'SPORT');