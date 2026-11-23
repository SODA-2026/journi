-- V7: Lägger till short_description på cities och fyller på short_description
-- och description för alla places som seedades i V5.
-- Kolumnerna short_description och description finns redan på places (skapades i V4),
-- så för places behövs bara UPDATE. Kräver MySQL 8.0.16+.

-- =====================================================
-- 1. Ny kolumn på cities
-- =====================================================
ALTER TABLE `cities`
    ADD COLUMN `short_description` VARCHAR(100) NULL DEFAULT NULL AFTER `name`;

-- =====================================================
-- 2. Fyll cities.short_description
-- =====================================================
UPDATE cities SET short_description = 'Sveriges huvudstad, byggd på öar mellan Mälaren och Östersjön' WHERE name = 'Stockholm';
UPDATE cities SET short_description = 'Västkustens hamnstad med nöjesparker, kanaler och skärgård' WHERE name = 'Göteborg';
UPDATE cities SET short_description = 'Mångkulturell stad vid Öresund med Turning Torso och hamnpromenader' WHERE name = 'Malmö';
UPDATE cities SET short_description = 'Universitets- och domkyrkostad med rötter i vikingatiden' WHERE name = 'Uppsala';
UPDATE cities SET short_description = 'Medeltida Hansestad på Gotland med ringmur och ruiner' WHERE name = 'Visby';
UPDATE cities SET short_description = 'Gammal universitetsstad med domkyrka, studentliv och parker' WHERE name = 'Lund';
UPDATE cities SET short_description = 'Gruvstad i Lappland med norrsken, midnattssol och Icehotel i närheten' WHERE name = 'Kiruna';
UPDATE cities SET short_description = 'Norrlands största stad, känd för kultur, studentliv och björkar' WHERE name = 'Umeå';
UPDATE cities SET short_description = 'Stad vid Svartån med vattenslott, Svampen och mycket grönska' WHERE name = 'Örebro';
UPDATE cities SET short_description = 'Hamnstad vid Öresund med Kärnan, Sofiero och färja till Danmark' WHERE name = 'Helsingborg';

UPDATE cities SET short_description = 'Norges huvudstad vid fjorden, med museer, opera och skog inpå knuten' WHERE name = 'Oslo';
UPDATE cities SET short_description = 'Fjordstad omgiven av sju berg, känd för Bryggen och regn' WHERE name = 'Bergen';
UPDATE cities SET short_description = 'Historisk stad med Nidarosdomen, trähus och livlig studentkultur' WHERE name = 'Trondheim';
UPDATE cities SET short_description = 'Oljestad vid Lysefjorden, nära Preikestolen och Kjerag' WHERE name = 'Stavanger';
UPDATE cities SET short_description = 'Nordnorges port med norrsken, midnattssol och arktisk natur' WHERE name = 'Tromsö';
UPDATE cities SET short_description = 'Jugendstadens pärla på öar vid havet, nära fjordarna' WHERE name = 'Ålesund';
UPDATE cities SET short_description = 'Sörlandets huvudstad med djurpark, trähus och skärgård' WHERE name = 'Kristiansand';
UPDATE cities SET short_description = 'Nordnorsk stad norr om polcirkeln, nära Saltstraumen och Lofoten' WHERE name = 'Bodö';
UPDATE cities SET short_description = 'Olympisk stad vid Mjösa med skidåkning, kultur och familjeäventyr' WHERE name = 'Lillehammer';
UPDATE cities SET short_description = 'Finnmarks största stad med hällristningar, norrsken och vildmark' WHERE name = 'Alta';

UPDATE cities SET short_description = 'Danmarks huvudstad med kanaler, slott, design och gott käk' WHERE name = 'Köpenhamn';
UPDATE cities SET short_description = 'Jylländsk universitetsstad med konst, hamn och livlig kultur' WHERE name = 'Århus';
UPDATE cities SET short_description = 'H.C. Andersens födelsestad på Fyn, full av sagor och historia' WHERE name = 'Odense';
UPDATE cities SET short_description = 'Nordjyllands huvudstad med uteliv, kultur och djurpark' WHERE name = 'Ålborg';
UPDATE cities SET short_description = 'Danmarks västkusthamn, grindar till Fanø och Vadehavet' WHERE name = 'Esbjerg';
UPDATE cities SET short_description = 'Vikingastad vid fjorden med domkyrka, skepp och musikfestival' WHERE name = 'Roskilde';
UPDATE cities SET short_description = 'Hamlets stad vid Öresund med Kronborg och färja till Sverige' WHERE name = 'Helsingör';
UPDATE cities SET short_description = 'Danmarks äldsta stad, med medeltida gator och Vadehavet nära' WHERE name = 'Ribe';
UPDATE cities SET short_description = 'Legos hemstad, med Legoland och LEGO House' WHERE name = 'Billund';
UPDATE cities SET short_description = 'Danmarks nordspets med ljus, konstnärer och vandrande sand' WHERE name = 'Skagen';

-- =====================================================
-- 2b. Två platser från V5 byts ut mot verkliga sevärdheter
--     (Frederiks Hus gick inte att verifiera som sevärdhet, och
--      Alta kyrka är samma byggnad som Norrskenskatedralen).
-- =====================================================
UPDATE places p JOIN cities c ON c.id = p.city_id
SET p.name = 'Sculpturepark Billund'
WHERE c.name = 'Billund' AND p.name = 'Frederiks Hus';

UPDATE places p JOIN cities c ON c.id = p.city_id
SET p.name = 'Halddeobservatoriet'
WHERE c.name = 'Alta' AND p.name = 'Alta kyrka';

-- =====================================================
-- 3. Fyll places.short_description och places.description
--    via en temporär tabell som kopplas mot (stad, plats).
-- =====================================================
CREATE TEMPORARY TABLE place_texts (
                                       city_name VARCHAR(255) NOT NULL,
                                       place_name VARCHAR(255) NOT NULL,
                                       short_description VARCHAR(100) NOT NULL,
                                       description TEXT NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

INSERT INTO place_texts (city_name, place_name, short_description, description) VALUES
-- ---------------- SVERIGE ----------------
-- Stockholm
('Stockholm', 'Gamla stan', 'Stockholms medeltida stadskärna', 'Stadens äldsta stadsdel ligger på Stadsholmen och har rötter i 1200-talet. Här finns kullerstensgator, smala gränder som Mårten Trotzigs gränd, Stortorget med Nobelmuseet och Storkyrkan. Området är fullt av kaféer, restauranger och hantverksbutiker.'),
('Stockholm', 'Vasamuseet', 'Museum med det bevarade regalskeppet Vasa', 'Regalskeppet Vasa sjönk på sin jungfrufärd i Stockholms hamn 1628 och bärgades 1961. Skeppet är till största delen original och ett av världens bäst bevarade fartyg från 1600-talet. Utställningarna visar livet ombord och hur skeppet räddades.'),
('Stockholm', 'Skansen', 'Friluftsmuseum och djurpark på Djurgården', 'Skansen grundades 1891 och är världens äldsta friluftsmuseum, med gårdar, hantverksmiljöer och en stadsdel från hela Sverige. I djurparken bor nordiska djur som björn, varg, lodjur och älg. Under året firas traditioner som midsommar och Lucia.'),
('Stockholm', 'ABBA The Museum', 'Interaktivt museum om popgruppen ABBA', 'Museet på Djurgården öppnade 2013 och visar scenkläder, guldskivor och en återskapad studio. Besökarna kan själva stiga upp på scen och sjunga tillsammans med virtuella ABBA-medlemmar.'),
('Stockholm', 'Djurgården', 'Grön ö med museer och natur mitt i stan', 'Kungliga Djurgården är en del av Ekoparken och en av Stockholms mest populära platser för promenader och cykling. Här ligger Gröna Lund, Skansen, Vasamuseet, Nordiska museet och Prins Eugens Waldemarsudde. Rosendals trädgård är ett populärt mål för fika och äpplen.'),
('Stockholm', 'Kungliga slottet', 'Kungens officiella residens och barockslott', 'Slottet byggdes efter branden 1697 och ritades av Nicodemus Tessin d.y. Med över 600 rum är det ett av Europas största slott som fortfarande används. Besökare kan se Representationsvåningarna, Skattkammaren, Livrustkammaren och högvaktens avlösning.'),
('Stockholm', 'Stadshuset', 'Röda tegelbyggnaden där Nobelbanketten hålls', 'Stadshuset vid Riddarfjärden ritades av Ragnar Östberg och invigdes 1923. Nobelbanketten hålls varje år i Blå hallen, och i Gyllene salen täcks väggarna av guldmosaik. Tornet är över 100 meter högt och ger en fin utsikt över staden.'),
('Stockholm', 'Fotografiska', 'Museum för samtida fotografi vid vattnet', 'Fotografiska ligger i en gammal tullbyggnad vid Stadsgårdskajen och öppnade 2010. Museet visar skiftande utställningar med fotografer från hela världen. Här finns också restaurang och bar med utsikt över Saltsjön.'),
('Stockholm', 'Moderna museet', 'Statens museum för modern och samtida konst', 'Museet ligger på Skeppsholmen i en byggnad ritad av Rafael Moneo. Samlingarna innehåller verk av bland andra Picasso, Dalí, Matisse och Warhol tillsammans med svensk och internationell samtidskonst.'),
('Stockholm', 'Drottningholms slott', 'Kungafamiljens bostad och världsarvsslott', 'Slottet på Lovön byggdes på 1600-talet efter förebild från Versailles och är Unesco-världsarv sedan 1991. Kungafamiljen bor här, men delar är öppna för besökare. I parken finns Kinesiska slottet och Slottsteatern från 1766, som har kvar sin ursprungliga scenmaskineri.'),
-- Göteborg
('Göteborg', 'Liseberg', 'Skandinaviens största nöjespark', 'Liseberg öppnade 1923 och är en av Nordens mest besökta nöjesparker. Här finns berg-och-dalbanor som Helix och Balder, lummiga trädgårdar och barnvänliga attraktioner. Under advent förvandlas parken till en stor julmarknad.'),
('Göteborg', 'Haga', 'Charmigt kvarter med trähus och kaféer', 'Stadsdelen Haga är känd för sina välbevarade 1800-talshus och landshövdingehus. Längs Haga Nygata ligger små butiker, hantverkare och kaféer som är kända för sina enorma kanelbullar, Hagabullar. Intill ligger Skansen Kronan med utsikt över staden.'),
('Göteborg', 'Slottsskogen', 'Stor stadspark med djur och lekplatser', 'Slottsskogen är Göteborgs största park och har en gratis djurpark där man kan se älgar, sälar, pingviner och många andra djur. Parken har också lekplatser, naturstigar och är platsen för festivalen Way Out West.'),
('Göteborg', 'Universeum', 'Vetenskapscentrum med regnskog och akvarier', 'Universeum öppnade 2001 och är ett av Skandinaviens största science center. Besökare går genom en tropisk regnskog med fritt flygande djur, ser akvarier och provar experiment som passar både barn och vuxna.'),
('Göteborg', 'Göteborgs konstmuseum', 'Konstmuseum vid Götaplatsen', 'Museet ligger vid Götaplatsen, där Carl Milles staty Poseidon står utanför. Samlingarna spänner från äldre mästare och fransk 1800-talskonst till nordisk modernism, med verk av bland andra Rembrandt, Van Gogh och Picasso.'),
('Göteborg', 'Feskekôrka', 'Fiskhall i en kyrkliknande byggnad', 'Fiskhallen invigdes 1874 och fick sitt smeknamn eftersom den liknar en kyrka. Här kan man köpa färsk fisk och skaldjur, och i hallen finns även en fiskrestaurang. Byggnaden ligger vid Rosenlundskanalen.'),
('Göteborg', 'Avenyn', 'Göteborgs paradgata', 'Kungsportsavenyn anlades på 1800-talet efter kontinentala förebilder och är cirka en kilometer lång. Den kantas av träd, restauranger, barer och butiker, och leder från Kungsportsplatsen upp till Götaplatsen.'),
('Göteborg', 'Älvsborgs fästning', 'Fästning på en ö vid Göteborgs inlopp', 'Nya Älvsborg byggdes på 1600-talet på Kyrkogårdsholmen för att försvara inloppet till Göta älv. Fästningen har varit både försvarsverk och fängelse. Man tar sig dit med turbåt, och ön har café och guidade visningar.'),
('Göteborg', 'Botaniska trädgården', 'En av Europas största botaniska trädgårdar', 'Göteborgs botaniska trädgård omfattar omkring 175 hektar och har ungefär 16 000 växtarter. Berömd är klippträdgården och den japanska dalen. Här finns också stora växthus och ett arboretum.'),
('Göteborg', 'Södra skärgården (t.ex. Styrsö)', 'Bilfria öar med bad och havsutsikt', 'Södra skärgården består av öar som Styrsö, Donsö, Brännö och Vrångö, och nås med färja från Saltholmen. Öarna är bilfria och passar för bad, promenader, cykling och fisk- och skaldjursluncher.'),
-- Malmö
('Malmö', 'Turning Torso', 'Skandinaviens högsta bostadshus', 'Den vridna skyskrapan i Västra hamnen ritades av Santiago Calatrava och stod klar 2005. Byggnaden är 190 meter hög och vrider sig 90 grader från botten till toppen. Den är en av Malmös mest kända symboler.'),
('Malmö', 'Malmöhus slott', 'Renässansslott med museer', 'Slottet byggdes på 1530-talet av Kristian III och är Nordens äldsta bevarade renässansfästning. Det omges av en vallgrav och rymmer Malmö Museer med konst, kulturhistoria och naturhistoria.'),
('Malmö', 'Stortorget', 'Stadens största torg', 'Stortorget anlades på 1500-talet och domineras av Rådhuset från 1546 och ryttarstatyn av Karl X Gustav. Intill ligger Jörgen Kocks hus och flera av stadens äldsta byggnader.'),
('Malmö', 'Lilla torg', 'Mysigt torg med restauranger och uteserveringar', 'Lilla torg är omgivet av korsvirkeshus från 1500-talet och är känt för sina restauranger och uteserveringar. Torget är en populär plats för middag, drinkar och sommarkvällar och ligger bara några minuter från Stortorget.'),
('Malmö', 'Ribersborgs kallbadhus', 'Kallbadhus med bastu och havsbad', 'Kallbadhuset byggdes 1898 och ligger på en brygga ute i Öresund. Här finns bastu uppdelad för kvinnor och män, havsbad och ett populärt café med utsikt över sundet och Öresundsbron.'),
('Malmö', 'Västra hamnen', 'Modern stadsdel vid vattnet', 'Västra hamnen är ett före detta varvsområde som blev en ny stadsdel i samband med bostadsmässan Bo01. Här finns modern arkitektur, stränder, strandpromenad och Turning Torso.'),
('Malmö', 'Moderna Museet Malmö', 'Utställningslokal för samtida konst', 'Malmöfilialen till Moderna Museet visar samtidskonst och ligger i en före detta elverkshall. Utställningarna ändras regelbundet och ger ofta nya perspektiv på nutida konst.'),
('Malmö', 'Folkets park', 'Nöjespark och grönområde i centrala Malmö', 'Folkets park öppnade 1891 och är Sveriges äldsta folkpark. Här finns lekplatser, scener, ett mindre djurområde och evenemang året runt, bland annat konserter och barnaktiviteter.'),
('Malmö', 'Slottsparken', 'Grön oas bredvid Malmöhus', 'Slottsparken har dammar, kanaler och gamla träd och ligger intill Malmöhus. Här finns Slottsmöllan och flera kaféer, och parken är en populär plats för promenader, picknick och lek.'),
('Malmö', 'Malmö Saluhall', 'Matmarknad med lokala och internationella smaker', 'Malmö Saluhall öppnade 2016 och samlar restauranger, delikatessbutiker och matstånd under ett tak. Utbudet speglar stadens mångkulturella matscen med allt från skånska råvaror till internationella rätter.'),
-- Uppsala
('Uppsala', 'Uppsala domkyrka', 'Nordens största kyrkobyggnad', 'Den gotiska domkyrkan byggdes mellan 1200- och 1400-talen och är cirka 119 meter hög. Här vilar bland annat Gustav Vasa, Erik den helige och Carl von Linné. Domkyrkan är också platsen där svenska kungar tidigare kröntes.'),
('Uppsala', 'Uppsala slott', 'Vasaslott på Slottsbacken', 'Slottet påbörjades av Gustav Vasa på 1540-talet och ligger på en höjd med utsikt över staden. Där finns bland annat Uppsala konstmuseum, och slottet har haft stor betydelse i svensk politisk historia.'),
('Uppsala', 'Gamla Uppsala', 'Fornlämningsområde med kungshögar', 'Gamla Uppsala är ett av Sveriges viktigaste fornlämningsområden, med tre stora kungshögar från 500-talet, ett museum och en medeltida kyrka. Platsen var ett religiöst och politiskt centrum under vikingatiden.'),
('Uppsala', 'Carolina Rediviva', 'Universitetsbiblioteket med Silverbibeln', 'Universitetsbiblioteket byggdes 1778 och rymmer unika skatter. Mest berömd är Codex Argenteus, den så kallade Silverbibeln från 500-talet, som är skriven på gotiska med silverbläck.'),
('Uppsala', 'Linnéträdgården', 'Barockträdgård tillägnad Carl von Linné', 'Linnéträdgården grundades 1655 och blev berömd när Carl von Linné ordnade den på 1700-talet. Sveriges äldsta botaniska trädgård har återskapats efter Linnés egna ritningar och visar hundratals växter i systematiska rabatter.'),
('Uppsala', 'Linnés Hammarby', 'Carl von Linnés sommarbostad', 'Hammarby var Carl von Linnés lantställe utanför Uppsala. Här kan man se det välbevarade huset, trädgården och Linnés museum, och få en bild av hur den världsberömde botanikern levde och arbetade.'),
('Uppsala', 'Fyrisån', 'Å som rinner genom staden', 'Fyrisån rinner genom centrala Uppsala och är kantad av promenadstråk, broar och kaféer. Under sommaren kan man hyra kanot eller äta på någon av uteserveringarna längs vattnet.'),
('Uppsala', 'Botaniska trädgården', 'Universitetets botaniska trädgård', 'Uppsala botaniska trädgård ligger intill slottet och har växthus, orangeri och varierande planteringar. Här kan man se växter från hela världen och få en lugn paus mitt i stan.'),
('Uppsala', 'Upplandsmuseet', 'Regionalmuseum om Upplands historia', 'Upplandsmuseet ligger i gamla kvarnbyggnader vid Fyrisån och berättar om Upplands historia och kultur. Utställningarna visar allt från forntid och medeltid till vardagsliv i modern tid.'),
('Uppsala', 'Gustavianum', 'Universitetets museum med anatomisk teater', 'Gustavianum är Uppsala universitets äldsta byggnad, uppförd 1625. Här finns Augsburgskåpet från 1600-talet, vikingatida föremål och den anatomiska teatern från 1662, där dissektioner förr utfördes inför publik.'),
-- Visby
('Visby', 'Ringmuren', 'Medeltida stadsmur kring Visby', 'Ringmuren byggdes på 1200-talet och är omkring 3,4 kilometer lång. Det finns ett trettiotal torn kvar. Muren är en viktig del av Visby som är Unesco-världsarv sedan 1995.'),
('Visby', 'Sankta Maria domkyrka', 'Visbys domkyrka', 'Kyrkan invigdes 1225 och byggdes av tyska köpmän. Den är den enda av stadens medeltidskyrkor som fortfarande används och blev domkyrka 1572. Interiören är rik på medeltida detaljer.'),
('Visby', 'S:t Karins ruin', 'Medeltida klosterruin', 'Ruinen efter franciskanernas klosterkyrka är en av Visbys mest stämningsfulla platser. Murarna står kvar mot himlen, och ruinen används ibland för konserter och teater.'),
('Visby', 'Botaniska trädgården', 'Lummig trädgård vid ringmuren', 'Visbys botaniska trädgård ligger intill ringmuren och är ett lugnt grönområde med träd, rosor och växter från flera världsdelar. Den är en populär plats för promenader och en paus mellan sevärdheterna.'),
('Visby', 'Gotlands Museum', 'Länsmuseum med bildstenar och silverskatter', 'Gotlands Museum, även kallat Fornsalen, visar öns historia från stenåldern till i dag. Samlingarna har bildstenar, vikingatida silverskatter och gotländska föremål från medeltiden.'),
('Visby', 'Almedalen', 'Park vid havet', 'Almedalen är en park nedanför ringmuren med utsikt över Östersjön. Här hålls varje sommar Almedalsveckan, där politiker och organisationer samlas.'),
('Visby', 'Kruttornet', 'Medeltida torn i stadsmuren', 'Kruttornet är ett av tornen i Visbys ringmur och ligger vid Almedalen. Tornet är en populär fotoplats och påminner om stadens försvarsverk från medeltiden.'),
('Visby', 'Galgberget', 'Utsiktsplats ovanför Visby', 'Namnet kommer från den tid då stadens avrättningsplats låg här. I dag är Galgberget en höjd med utsikt över staden, ringmuren och havet, och passar bra för en kvällspromenad.'),
('Visby', 'Hamnen', 'Visbys hamn', 'Visbys hamn är ett livligt område med restauranger, kajer och turistbåtar. Härifrån går färjorna till Nynäshamn och Oskarshamn, och på sommaren är hamnen full av liv.'),
('Visby', 'Stora torget', 'Torg i centrala Visby', 'Stora Torget är ett av stadens centrala torg och omges av kaféer, butiker och medeltida byggnader. Under Medeltidsveckan och andra evenemang är torget en samlingspunkt.'),
-- Lund
('Lund', 'Lunds domkyrka', 'Romansk katedral från 1100-talet', 'Domkyrkan invigdes 1145 och är en av Nordens främsta romanska byggnader. Berömd är det astronomiska uret Horologium mirabile från 1400-talet och kryptan, med bland annat pelaren med Finn jätten.'),
('Lund', 'Kulturen', 'Friluftsmuseum i centrala Lund', 'Kulturen grundades 1892 och är ett av världens äldsta friluftsmuseer. Museet består av omkring 30 byggnader och trädgårdar och visar Skånes historia, med utställningar om vardagsliv, konst och hantverk.'),
('Lund', 'Lundagård', 'Parken framför universitetet', 'Lundagård är parken mellan domkyrkan och universitetet och stadens gröna hjärta. Här samlas studenter och lundabor, och under Lundakarnevalen förvandlas området till en stor festplats.'),
('Lund', 'Botaniska trädgården', 'Universitetets botaniska trädgård', 'Lunds botaniska trädgård grundades 1690 och har växthus och planteringar med tusentals växtarter. Trädgården är öppen för alla och är ett lugnt avbrott i universitetsstaden.'),
('Lund', 'Skissernas museum', 'Museum för skisser och konstnärers förarbeten', 'Skissernas museum visar skisser, modeller och förarbeten till offentlig konst från hela världen. Det är unikt i Norden och ger en inblick i hur konstverk blir till.'),
('Lund', 'Stadsparken', 'Grön park i centrala Lund', 'Stadsparken anlades på 1890-talet och har dammar, stigar och blommande planteringar. Den är en populär plats för promenader och picknick och har en scen där evenemang ordnas på sommaren.'),
('Lund', 'Historiska museet', 'Universitetets historiska museum', 'Museet visar föremål och berättelser om Skånes historia från forntid till medeltid. Samlingarna speglar hur regionen har förändrats under tusentals år.'),
('Lund', 'Mejeriet', 'Musikscen och klubb i Lund', 'Mejeriet är en av Lunds mest kända kulturscener, med livemusik, klubbkvällar och evenemang. Många kända artister har spelat här, och stället är populärt bland studenter.'),
('Lund', 'Lunds universitet', 'Ett av Nordens äldsta och största universitet', 'Universitetet grundades 1666 och är ett av Nordens mest anrika lärosäten. Universitetshuset från 1800-talet ligger vid Lundagård, och universitetet präglar hela stadens liv.'),
('Lund', 'Observatoriet', 'Astronomiskt observatorium', 'Observatoriet hör till universitetets astronomiska verksamhet. Det har historiska instrument, och publika visningar och föredrag ordnas ibland för den som är intresserad av stjärnor.'),
-- Kiruna
('Kiruna', 'Kiruna kyrka', 'Stor träkyrka inspirerad av en samisk kåta', 'Kyrkan ritades av Gustaf Wickman och invigdes 1912. Formen är inspirerad av en samisk kåta och en stavkyrka, och kyrkan är en av Sveriges största träbyggnader. Den flyttades 2025 i samband med stadens omlokalisering.'),
('Kiruna', 'Stadshuset', 'Stadshus från 1960-talet med klockspel', 'Stadshuset ritades av Artur von Schmalensee och invigdes på 1960-talet. Byggnaden är känd för sin arkitektur och sitt klocktorn med klockspel, och är en symbol för det gamla Kiruna.'),
('Kiruna', 'Gruvbesök (LKAB)', 'Guidad tur i en av världens största underjordsgruvor', 'LKAB:s järnmalmsgruva i Kiruna är en av världens största underjordsgruvor. Under en guidad tur åker man ner i berget och får se hur malmen bryts och hur gruvan har format staden.'),
('Kiruna', 'Hjalmar Lundbohmsgården', 'Museum i gruvbolagets förste disponents hem', 'Huset var Hjalmar Lundbohms bostad. Han var gruvbolagets förste disponent och en stor kulturvän. Byggnaden berättar om stadens tidiga historia och om Kirunas grundande.'),
('Kiruna', 'Luossavaara', 'Berg med motionsspår och utsikt', 'Luossavaara är berget strax utanför stan, med skidbacke och motionsspår som används både sommar och vinter. Från toppen ser man Kiruna och den omgivande fjällvärlden, och under sommaren midnattssolen.'),
('Kiruna', 'Jukkasjärvi Ishotell', 'Hotell byggt av is och snö', 'Icehotel i Jukkasjärvi byggdes första gången 1989 och byggs om varje vinter av is från Torneälven. Gästerna kan övernatta i isrum och se konstverk gjorda av is och snö.'),
('Kiruna', 'Abisko nationalpark', 'Nationalpark med bra chans att se norrsken', 'Abisko nationalpark ligger vid Torneträsk och är känd för sitt torra och klara väder. Det gör parken till en av världens bästa platser att se norrsken. Här börjar också Kungsleden.'),
('Kiruna', 'Aurora Sky Station', 'Norrskensplats på berget Nuolja', 'Aurora Sky Station nås med stolslift upp på berget Nuolja nära Abisko. Därifrån har man utsikt över Torneträsk och kan se norrskenet utan stadsljus.'),
('Kiruna', 'Kebnekaise', 'Sveriges högsta fjäll', 'Kebnekaise är omkring 2 100 meter högt och Sveriges högsta fjäll. Toppen krymper när glaciären smälter, och bergsvandringen upp lockar många vandrare varje sommar.'),
('Kiruna', 'Torneträsk', 'Stor sjö i Lappland', 'Torneträsk är en av Sveriges största sjöar och omges av fjäll och orörd natur. Sjön ligger vid Abisko och är ett populärt mål för vandring, fiske och norrsken.'),
-- Umeå
('Umeå', 'Bildmuseet', 'Konstmuseum med modern trärkitektur', 'Bildmuseet är Umeå universitets museum för samtidskonst och visuell kultur. Byggnaden vid Umeälven ritades av Henning Larsen Architects och har en vacker träfasad. Museet har utställningar, café och en fin utsikt över älven.'),
('Umeå', 'Gammlia friluftsmuseum', 'Friluftsmuseum med gamla gårdar', 'Gammlia visar hur människor levde i Västerbotten förr. Här finns gamla gårdar, hus och trädgårdar, och museet ger en bra inblick i norrländskt vardagsliv genom tiderna.'),
('Umeå', 'Guitars – The Museum', 'Museum om gitarrer och musik', 'Museet visar en stor samling gitarrer från olika epoker och berättar om instrumentets historia och betydelse för musiken. Det passar både musikintresserade och nyfikna besökare.'),
('Umeå', 'Väven', 'Kulturhus med bibliotek och utställningar', 'Väven är Umeås kulturhus och har bibliotek, utställningar och restaurang. Huset är en mötesplats för kultur och hör till stadens moderna landmärken.'),
('Umeå', 'Norrlandsoperan', 'Opera- och konserthus vid älven', 'Norrlandsoperan är en av landets främsta kulturinstitutioner i norr, med opera, konserter och teater. Huset ligger nära Umeälven och har en fin arkitektur.'),
('Umeå', 'Umeälven', 'Älv som rinner genom staden', 'Umeälven rinner genom Umeå och har promenadstråk, broar och fina utsiktsplatser. Efter branden 1888 planterades björkar längs gatorna, och Umeå kallas därför Björkarnas stad.'),
('Umeå', 'Umedalen Skulptur', 'Skulpturpark med samtida konst', 'Umedalen Skulptur är en utomhusutställning med dussintals samtida skulpturer i en parkmiljö utanför centrum. Man kan promenera fritt bland konstverken och njuta av naturen.'),
('Umeå', 'Hamnmagasinet', 'Kulturlokal vid hamnen', 'Hamnmagasinet är en kulturscen i Umeå där man ordnar konserter, evenemang och utställningar. Lokalen är en populär mötesplats för kulturintresserade.'),
('Umeå', 'Nydalasjön', 'Sjö med motionsspår och badplats', 'Nydalasjön är ett populärt friluftsområde nära centrum, med promenadstig, motionsspår och badplats. Området används flitigt av både joggare, badare och familjer.'),
('Umeå', 'Holmön', 'Skärgårdsö utanför Umeå', 'Holmön är en skärgårdsö som nås med färja från Norrbyn. Ön är känd för sin natur, sitt fågelliv och fina vandringsleder, och passar bra för en dagstur eller en lugn helg.'),
-- Örebro
('Örebro', 'Örebro slott', 'Medeltida slott på en ö i Svartån', 'Slottet ligger på en ö i Svartån mitt i staden och har anor från medeltiden. Här hölls riksdagen 1810 då Jean Baptiste Bernadotte valdes till svensk tronföljare.'),
('Örebro', 'Svampen', 'Vattentorn med utsiktsplats och café', 'Svampen är ett vattentorn från 1958 som blivit Örebros symbol. Från utsiktsplatsen i toppen ser man ut över staden och Hjälmaren, och i tornet finns café och restaurang.'),
('Örebro', 'Wadköping', 'Gammaldags stadsmiljö med flyttade trähus', 'Wadköping är ett kulturreservat med flyttade trähus från 1800-talet. Namnet kommer från Hjalmar Bergmans romaner, och här finns hantverkare, kaféer och museer.'),
('Örebro', 'Stadsparken', 'Park i centrala Örebro', 'Stadsparken är en grön plats i hjärtat av staden, med promenadstigar, planteringar och evenemang under sommaren. Den är en populär mötesplats för örebroare.'),
('Örebro', 'Engelbrektsstatyn', 'Staty av frihetshjälten Engelbrekt', 'Statyn föreställer Engelbrekt Engelbrektsson, som ledde ett uppror mot kung Erik av Pommern på 1430-talet. Örebro var en viktig plats i hans historia, och statyn är ett viktigt monument.'),
('Örebro', 'Rådhuset', 'Historisk byggnad i centrum', 'Rådhuset är en historisk byggnad i stadens centrum och hör till de vackra äldre byggnader som präglar Örebro.'),
('Örebro', 'Örebro konsthall', 'Utställningar med samtida konst', 'Konsthallen visar samtidskonst av svenska och internationella konstnärer. Utställningarna växlar och ger nya perspektiv på nutida konst i centrala Örebro.'),
('Örebro', 'Hjälmaren', 'Sveriges fjärde största sjö', 'Hjälmaren är Sveriges fjärde största sjö och en populär plats för fiske, bad och båtliv. Hjälmare kanal förbinder sjön med Mälaren, och Örebro ligger vid dess västra ände.'),
('Örebro', 'Länsmuseet', 'Museum om Närkes historia', 'Länsmuseet visar Närkes historia och kultur med utställningar om forntid, medeltid och nutid. Det är en bra start för den som vill förstå regionen.'),
('Örebro', 'Karlslund herrgård', 'Herrgård vid Svartån', 'Karlslund herrgård ligger vid Svartån och är en populär utflyktsplats med park och café. Här kan man promenera, fika och njuta av den historiska miljön.'),
-- Helsingborg
('Helsingborg', 'Kärnan', 'Medeltida fästningstorn', 'Kärnan är ett medeltida torn från 1300-talet, cirka 35 meter högt, och en av Skånes mest kända landmärken. Från toppen har man utsikt över Öresund och Danmark.'),
('Helsingborg', 'Sofiero slott', 'Slott med stor trädgård', 'Sofiero slott byggdes på 1860-talet som sommarresidens åt Oscar II och Sofia. Slottet är känt för sin rododendronpark, som blommar i maj och juni, och sina sommarträdgårdar med många rosor och perenner.'),
('Helsingborg', 'Fredriksdal', 'Friluftsmuseum och trädgårdar', 'Fredriksdal är ett friluftsmuseum med gamla byggnader, trädgårdar och djur. Här finns stadsmiljö från 1800-talet, en teater och aktiviteter för hela familjen.'),
('Helsingborg', 'Dunkers kulturhus', 'Kulturhus vid hamnen', 'Dunkers kulturhus öppnade 2002 och ritades av Kim Utzon. Huset har utställningar, konserter och kulturhistoriska samlingar, och ligger vackert vid hamnen.'),
('Helsingborg', 'Mariakyrkan', 'Gotisk tegelkyrka från 1300-talet', 'Mariakyrkan är en gotisk tegelkyrka från 1300-talet och en av stadens äldsta byggnader. Kyrkan ligger i hjärtat av Helsingborg och har många historiska detaljer.'),
('Helsingborg', 'Terrassen', 'Monumental trappa i stadens centrum', 'Terrassen är en stor trappanläggning från 1900-talets början som leder upp från Stortorget. Därifrån har man fina utsikter över sundet och de gamla byggnaderna.'),
('Helsingborg', 'Hamnen', 'Helsingborgs hamn', 'Hamnen har promenadstråk, restauranger och färjor till Helsingör. Området är en bra plats för en promenad och för att titta på fartygen i Öresund.'),
('Helsingborg', 'Pålsjö skog', 'Skogsområde nära havet', 'Pålsjö skog ligger vid Öresund och har promenadstigar och möjlighet till bad. Skogen är ett populärt utflyktsmål nära centrum och är lugn och skön året om.'),
('Helsingborg', 'Rådhuset', 'Rådhus från 1897 med torn', 'Rådhuset ritades av Alfred Hellerström och stod klart 1897. Byggnaden i tegel med torn är ett av stadens mest kända landmärken.'),
('Helsingborg', 'Färjan till Helsingör', 'Färjeförbindelse över Öresund', 'Färjan går över Öresund till Helsingör och tar cirka 20 minuter. Det är en populär dagstur, och överfarten ger fin utsikt över Kronborg och sundet.'),

-- ---------------- NORGE ----------------
-- Oslo
('Oslo', 'Operahuset', 'Marmorklätt operahus vid fjorden', 'Operahuset öppnade 2008 och ritades av arkitektbyrån Snøhetta. Byggnaden är klädd i vit marmor och granit och har ett lutande tak som besökare kan gå upp på för att njuta av utsikten över fjorden.'),
('Oslo', 'Vigelandsparken', 'Skulpturpark med verk av Gustav Vigeland', 'Vigelandsparken i Frognerparken innehåller över 200 skulpturer i brons, granit och smidesjärn av Gustav Vigeland. Mest berömd är Monolitten, som består av 121 sammanflätade människofigurer.'),
('Oslo', 'Vikingaskeppsmuseet', 'Museum med välbevarade vikingaskepp', 'Museet på Bygdøy rymmer Osebergs-, Gokstads- och Tuneskeppet, några av världens bäst bevarade vikingaskepp. Museet är stängt för ombyggnad och väntas öppna igen 2027 som Vikingtidsmuseet, så kontrollera aktuell status.'),
('Oslo', 'Akershus fästning', 'Medeltida fästning vid fjorden', 'Akershus fästning byggdes på 1290-talet av Håkon V och vakar över hamnen. Här finns ett slott, ett mausoleum för kungafamiljen, flera museer och en stor park med utsikt över fjorden.'),
('Oslo', 'Holmenkollen', 'Känd skidhoppsbacke med skimuseum', 'Holmenkollen är ett av Norges mest kända landmärken med skidhoppning sedan 1892. Backen byggdes om 2010. Här finns också Holmenkollens skimuseum, världens äldsta, och en zipline för äventyrliga besökare.'),
('Oslo', 'Munchmuseet', 'Museum för Edvard Munchs konst', 'Munchmuseet öppnade 2021 i Bjørvika och visar tusentals verk av Edvard Munch. Byggnaden är cirka 60 meter hög och har utsikt över fjorden och Operahuset.'),
('Oslo', 'Frammuseet', 'Museum om polarskeppet Fram', 'Museet på Bygdøy visar polarskeppet Fram, som användes av Fridtjof Nansen på expeditionen 1893–1896 och senare av Roald Amundsen på färden till Sydpolen 1910–1912. Man kan gå ombord på skeppet.'),
('Oslo', 'Nobels fredscenter', 'Centrum om Nobels fredspris', 'Nobels fredscenter ligger vid Rådhusplassen, där fredspriset delas ut varje år. Centret berättar om pristagarna och om arbetet för fred och mänskliga rättigheter.'),
('Oslo', 'Aker Brygge', 'Restaurang- och shoppingområde vid hamnen', 'Aker Brygge är ett före detta varvsområde som blivit ett populärt område med restauranger, barer och butiker. På sommaren är kajerna fulla av liv och utsikten över Oslofjorden är fin.'),
('Oslo', 'Nationalmuseet', 'Norges största konstmuseum', 'Nationalmuseet öppnade 2022 och är Nordens största museum för konst, arkitektur och design. Här finns bland annat Edvard Munchs Skriet från 1893 och många andra norska mästerverk.'),
-- Bergen
('Bergen', 'Bryggen', 'Hanseatiskt kvarter med färgglada trähus', 'Bryggen är Bergens gamla hamnkvarter och Unesco-världsarv sedan 1979. De färgglada trähusen byggdes efter branden 1702 och påminner om tiden då tyska Hansan dominerade handeln. Här finns museer, butiker och restauranger.'),
('Bergen', 'Fløibanen', 'Bergbana till utsikten på Fløyen', 'Fløibanen öppnade 1918 och tar besökare på cirka åtta minuter från centrum upp till Fløyen, 320 meter över havet. Därifrån har man utsikt över staden och fjorden, och här finns vandringsleder och lekplatser.'),
('Bergen', 'Fisktorget', 'Torg med fisk och skaldjur', 'Fisktorget ligger i hamnen och säljer fisk, skaldjur och lokala produkter. Här kan man äta lunch på plats och prova norska specialiteter som laxrullar och kungskrabba.'),
('Bergen', 'Ulriken', 'Berg med linbana och utsikt', 'Ulriken är det högsta av Bergens sju berg, 643 meter över havet. Man tar sig upp med linbana eller vandring och får en storslagen utsikt över staden, fjordarna och havet.'),
('Bergen', 'Bergenhus fästning', 'Fästning med Håkonshallen', 'Bergenhus fästning är en av Norges äldsta och rymmer den medeltida Håkonshallen från 1200-talet och Rosenkrantztornet från 1500-talet. Området är ett lugnt grönområde med utsikt över hamnen.'),
('Bergen', 'Troldhaugen', 'Edvard Griegs hem och museum', 'Troldhaugen var Edvard Griegs hem mellan 1885 och 1907. Här kan man besöka huset, Griegs komponistkoja och ett museum, och lyssna på konserter i konsertsalen Troldsalen.'),
('Bergen', 'KODE', 'Konstmuseer i centrala Bergen', 'KODE består av flera konstmuseer i centrala Bergen med samlingar av norsk och internationell konst. Rasmus Meyer-samlingen innehåller många verk av Edvard Munch.'),
('Bergen', 'Gamla Bergens museum', 'Friluftsmuseum med trähus', 'Museet består av omkring 50 trähus från 1700- och 1800-talen som flyttats hit. Man går genom smala gator och får se hur människor bodde och arbetade i Bergen förr.'),
('Bergen', 'Akvariet i Bergen', 'Akvarium med pingviner och sälar', 'Akvariet ligger på Nordnes och har pingviner, sälar och många fiskar och havsdjur. Det är ett bra mål för både barn och vuxna, och här hålls ofta matning och visningar.'),
('Bergen', 'Nordnes', 'Stadsdel och halvö med trähus', 'Nordnes är en halvö med smala gator, gamla trähus och Nordnesparken. Här finns också ett sjöbad och fina platser för en promenad vid fjorden, nära Akvariet.'),
-- Trondheim
('Trondheim', 'Nidarosdomen', 'Norges mest kända kyrka', 'Nidarosdomen byggdes mellan 1070 och 1300 över Sankt Olavs grav och är Nordens största medeltida byggnad. Domkyrkan är ett nationellt monument, och man kan klättra upp i tornet för utsikt över staden.'),
('Trondheim', 'Gamla stadsbron', 'Bro med röda portar', 'Den gamla stadsbron över Nidelva byggdes 1861 och leder till stadsdelen Bakklandet. Bron är känd för sin röda portal, Lykkens portal, och är ett populärt fotomotiv.'),
('Trondheim', 'Bakklandet', 'Trähuskvarter med kaféer', 'Bakklandet är ett charmigt kvarter med färgglada trähus, kaféer och butiker. Här finns även Trampe, en cykelhiss som hjälper cyklister uppför backen vid Brubakken.'),
('Trondheim', 'Kristiansten fästning', 'Fästning med utsikt', 'Kristiansten fästning byggdes efter stadsbranden 1681 och står på en kulle med utsikt över staden. Fästningen stod emot svenska trupper 1718 och är en viktig del av stadens historia.'),
('Trondheim', 'Ärkebiskopsgården', 'Medeltida palats vid domkyrkan', 'Ärkebiskopsgården är Nordens äldsta profana byggnad och ligger intill Nidarosdomen. Här finns museer och Norges riksregalier, kronan, spiran och riksäpplet.'),
('Trondheim', 'Ravnkloa', 'Fisktorg vid kanalen', 'Ravnkloa är ett fisktorg vid kanalen i centrala Trondheim. Härifrån avgår båtarna till Munkholmen, och här kan man köpa färsk fisk och skaldjur.'),
('Trondheim', 'Sverresborgs folkmuseum', 'Friluftsmuseum med gamla hus', 'Sverresborg Trøndelag folkemuseum har omkring 60 byggnader, bland annat en stavkyrka från Haltdalen. Här finns också resterna av en medeltida borg byggd av kung Sverre.'),
('Trondheim', 'Rockheim', 'Nationellt museum för populärmusik', 'Rockheim öppnade 2010 och berättar om norsk pop- och rockmusik från 1950-talet till i dag. Museet ligger vid hamnen och har interaktiva utställningar.'),
('Trondheim', 'Munkholmen', 'Klosterö i fjorden', 'Munkholmen är en liten ö i Trondheimsfjorden med ett före detta kloster från 1100-talet. Senare blev ön fästning och fängelse. Man tar båt från Ravnkloa på cirka tio minuter.'),
('Trondheim', 'Tyholttornet', 'Telekomtorn med utsikt', 'Tyholttornet är ett telekommunikationstorn på 124 meter, ett landmärke i Trondheim. Det ligger på Tyholt och syns från stora delar av staden.'),
-- Stavanger
('Stavanger', 'Preikestolen', 'Klippplatå ovanför Lysefjorden', 'Preikestolen är en klippplatå som ligger cirka 604 meter över Lysefjorden. Vandringen dit tar ungefär två timmar enkel väg, och utsikten över fjorden är en av Norges mest kända.'),
('Stavanger', 'Gamla Stavanger', 'Område med vita trähus', 'Gamla Stavanger har omkring 170 vita trähus från 1700- och 1800-talen längs smala kullerstensgator. Området är Nordens bäst bevarade trähusbebyggelse och berättar om sardinindustrins tid.'),
('Stavanger', 'Norska oljemuseet', 'Museum om Norges oljeindustri', 'Norska oljemuseet berättar om hur oljan i Nordsjön har format Norge. Byggnaden är inspirerad av en oljeplattform, och utställningarna visar allt från borrning till framtidens energi.'),
('Stavanger', 'Lysefjorden', 'Fjord med branta klippor', 'Lysefjorden är en cirka 42 kilometer lång fjord med branta klippväggar. Längs fjorden ligger Preikestolen och Kjerag, och man kan åka fjordkryssning eller vandra uppför de 4 444 trappstegen i Flørli.'),
('Stavanger', 'Stavanger domkyrka', 'Medeltida domkyrka från 1100-talet', 'Stavanger domkyrka påbörjades omkring 1125 och är Norges äldsta bevarade domkyrka. Kyrkan är romansk med gotiska inslag och ligger mitt i staden.'),
('Stavanger', 'Kjerag', 'Klippa med Kjeragbolten', 'Kjerag är ett berg vid Lysefjorden, 984 meter över fjorden. Mest känd är stenblocket Kjeragbolten, som sitter fastklämt mellan två klippväggar. Vandringen är krävande och tar cirka fem till sex timmar tur och retur.'),
('Stavanger', 'Svärd i berg', 'Monument över slaget vid Hafrsfjord', 'Monumentet består av tre stora svärd i berget vid Hafrsfjord och minner om slaget år 872, då Harald Hårfager enade Norge. Det invigdes 1983 och svärden är cirka tio meter höga.'),
('Stavanger', 'Breiavatnet', 'Sjö i stadens centrum', 'Breiavatnet är en liten sjö mitt i Stavanger, omgiven av park och byggnader. Här finns en promenadväg och den är en populär plats för avkoppling i staden.'),
('Stavanger', 'Utstein kloster', 'Medeltida kloster på Mosterøy', 'Utstein kloster på Mosterøy är Norges bäst bevarade medeltida kloster, grundat på 1200-talet. Här kan man besöka kyrkan och klosterbyggnaderna och få en bild av klosterlivet.'),
('Stavanger', 'Norska konservmuseet', 'Museum om konservindustrin', 'Museet berättar om konservindustrin i Stavanger, som under en period hade över femtio fabriker. Här kan man se hur sardiner konserverades och höra berättelser om arbetarna.'),
-- Tromsö
('Tromsö', 'Ishavskatedralen', 'Kyrka med modern arkitektur', 'Ishavskatedralen ritades av Jan Inge Hovig och invigdes 1965. Kyrkan består av elva trianglar i betong och har en stor glasmosaik på östsidan, gjord av Victor Sparre.'),
('Tromsö', 'Fjellheisen (bergbanan)', 'Linbana till Storsteinen', 'Linbanan går från Tromsdalen upp till Storsteinen, 421 meter över havet, på cirka fyra minuter. Därifrån har man utsikt över Tromsö, fjordarna och fjällen, och på vintern norrsken.'),
('Tromsö', 'Polaria', 'Akvarium om arktis', 'Polaria är ett akvarium och upplevelsecentrum om Arktis. Här finns skäggsälar och arktiska fiskar, och byggnaden liknar isflak som staplats på varandra.'),
('Tromsö', 'Polarmuseet', 'Museum om polarforskning', 'Polarmuseet ligger i ett gammalt tullhus vid hamnen och berättar om fångstmän, polarforskare och arktisk historia. Roald Amundsen och andra upptäcktsresande har en framträdande plats.'),
('Tromsö', 'Tromsöbron', 'Bro över sundet', 'Tromsöbron är cirka 1 000 meter lång och öppnade 1960. Den förbinder centrala Tromsö med fastlandet och leder till Ishavskatedralen.'),
('Tromsö', 'Norrskensturer', 'Turer för att se norrsken', 'Under säsongen från september till mars kan man följa med guidade turer ut från staden för att se norrsken. Guiderna tar dig dit där himlen är mörk och chansen att se norrsken är bäst.'),
('Tromsö', 'Sommarøy', 'Ö med vita stränder', 'Sommarøy ligger cirka en timme från Tromsö och är känd för sina vita sandstränder och klara vatten. Ön nås via bro och är populär på sommaren och för midnattssolen.'),
('Tromsö', 'Telegrafbukta', 'Strand nära centrum', 'Telegrafbukta är en strand i Tromsö som är populär för bad och solbad på sommaren. Den ligger nära centrum och har fin utsikt över sundet.'),
('Tromsö', 'Kvaløya', 'Ö med fjäll och natur', 'Kvaløya är en stor ö väster om Tromsö med dramatisk natur, fjäll och fjordar. Den är populär för vandring, och man tar sig dit via bro. Härifrån når man även Sommarøy.'),
('Tromsö', 'Tromsö botaniska trädgård', 'Botanisk trädgård långt i norr', 'Trädgården är en av världens nordligaste botaniska trädgårdar och visar arktiska och alpina växter. Den ligger i Tromsö och visar att många växter trivs även långt norr om polcirkeln.'),
-- Ålesund
('Ålesund', 'Jugendstilcentret', 'Museum om jugendstil', 'Jugendstilcentret ligger i det gamla Svaneapoteket från 1907 och berättar om jugendstilen och stadens återuppbyggnad efter den stora branden 1904. Staden är känd för sina jugendstilbyggnader.'),
('Ålesund', 'Aksla (Fjellstua)', 'Utsiktsplats över staden', 'Man når Aksla via 418 trappsteg från stadsparken och får en fantastisk utsikt över Ålesund, öarna och havet. Uppe vid Fjellstua finns också ett café.'),
('Ålesund', 'Atlanterhavsparken', 'Akvarium vid havet', 'Atlanterhavsparken är ett av Europas största saltvattensakvarier och ligger vid kusten. Här kan man se havsdjur och lära sig om naturen längs den norska kusten.'),
('Ålesund', 'Alnes fyr', 'Fyr på Godøya', 'Alnes fyr ligger vid havet på ön Godøya och byggdes 1852. Fyren har utsikt över havet och en liten konstutställning. Det är en populär plats för solnedgång.'),
('Ålesund', 'Giske', 'Ö med fiskelägen', 'Giske är en ö utanför Ålesund med charmiga fiskelägen och en medeltida marmorkyrka. Man når den via undersjöisk tunnel och bro.'),
('Ålesund', 'Runde fågelberg', 'Fågelö med lunnefåglar', 'Runde är en ö känd för sitt rika fågelliv och är hem för omkring en halv miljon sjöfåglar, inklusive lunnefåglar. Man kan ta båttur eller vandra och se fåglarna på nära håll.'),
('Ålesund', 'Geirangerfjorden', 'Fjord i Unescos världsarvslista', 'Geirangerfjorden är en av Norges mest berömda fjordar och finns på Unescos världsarvslista sedan 2005. Branta berg och vattenfall som De sju systrarna gör den till en storslagen upplevelse.'),
('Ålesund', 'Sunnmöre museum', 'Friluftsmuseum', 'Sunnmöre museum är ett av Norges största friluftsmuseer och visar omkring 50 gamla hus och båtar. Här finns även vikingaskeppsreplikor och miljöer från regionen.'),
('Ålesund', 'Brosundet', 'Kanal i centrum', 'Brosundet är kanalen som löper genom Ålesunds centrum, omgiven av jugendstilhus. Längs kajerna finns restauranger och caféer, och området är en populär promenadplats.'),
('Ålesund', 'Hjørundfjorden', 'Fjord omgiven av branta berg', 'Hjørundfjorden är en vacker fjord omgiven av branta berg som kallas Sunnmöre-alperna. Den är populär för vandring, kajak och för den stämningsfulla naturen.'),
-- Kristiansand
('Kristiansand', 'Djurparken', 'Djurpark med Kardemomme by', 'Dyreparken är Norges mest besökta turistmål och har både djurpark och nöjesfält. Här finns lejon, vargar och många andra djur samt Kardemomme by, byggd efter Thorbjørn Egners barnbok.'),
('Kristiansand', 'Posebyen', 'Område med gamla trähus', 'Posebyen är en del av Kvadraturen, stadens rutnät från 1641, och har trähus från 1700- och 1800-talen. Området är ett av Nordens största sammanhängande trähusområden.'),
('Kristiansand', 'Fiskebryggan', 'Fiskhall och restauranger', 'Fiskebrygga ligger vid hamnen och är känd för sina fiskrestauranger och sin fiskhall. Här kan man äta färsk fisk och skaldjur med havsutsikt.'),
('Kristiansand', 'Odderøya', 'Halvö med kultur och natur', 'Odderøya är en halvö nära centrum som tidigare var militärområde. I dag är den ett kulturområde med konstnärsateljéer, vandringsleder, badplatser och gamla fästningsanläggningar.'),
('Kristiansand', 'Kunstsilo', 'Konstmuseum i en gammal silo', 'Kunstsilo öppnade 2024 i en omgjord spannmålssilo vid hamnen. Museet visar Nicolai Tangens samling av nordisk modernism och samtidskonst.'),
('Kristiansand', 'Christiansholms fästning', 'Fästning från 1600-talet', 'Christiansholms fästning byggdes omkring 1672 i hamnen och är rund med tjocka murar. Fästningen skulle försvara staden och har kanoner kvar än i dag.'),
('Kristiansand', 'Ravnedalen', 'Park och dal med skog', 'Ravnedalen är en vacker park och dal nära centrum, med promenadstigar, vattenfall och konserter under sommaren. Den är en lugn plats mitt i staden.'),
('Kristiansand', 'Baneheia', 'Skogsområde nära centrum', 'Baneheia är stadens friluftsområde med skog, sjöar och badplatser. Området används för promenader, cykling och motion året om och kallas ofta stadens lunga.'),
('Kristiansand', 'Gimle gård', 'Empiregård med trädgård', 'Gimle gård är ett herrgårdshus i empirestil från 1800-talets början. Det har museum och en vacker trädgård och visar hur rika köpmän bodde i staden.'),
('Kristiansand', 'Markens gata', 'Stadens huvudgata', 'Markens gate är en livlig gågata i Kristiansand med butiker, kaféer och restauranger. Den ligger i Kvadraturen och är stadens huvudstråk.'),
-- Bodö
('Bodö', 'Saltstraumen', 'Kraftig tidvattenström', 'Saltstraumen är en av världens starkaste tidvattenströmmar. Ungefär 400 miljoner kubikmeter vatten pressas genom ett smalt sund var sjätte timme och skapar virvlar. Platsen är också bra för fiske.'),
('Bodö', 'Norska flygmuseet', 'Museum om flyg', 'Norsk Luftfartsmuseum ligger i en byggnad som liknar en flygplansmotor. Samlingarna visar norsk flyghistoria och kalla kriget, och här finns bland annat ett U-2-plan kopplat till Gary Powers.'),
('Bodö', 'Kjerringøy handelsplats', 'Gammal handelsplats', 'Kjerringøy handelssted är en välbevarad handelsplats från 1800-talet. Författaren Knut Hamsun arbetade här som ung, och platsen ger en bild av livet vid kusten förr.'),
('Bodö', 'Svartisen', 'Glaciär', 'Svartisen är Norges näst största glaciär. Man kan ta båt över sjön Svartisvatnet och vandra upp till glaciärarmen Engabreen, som sträcker sig nästan ner till havet.'),
('Bodö', 'Stormen kulturhus', 'Konserthus och bibliotek', 'Stormen kulturhus öppnade 2014 och rymmer bibliotek och konserthus. Byggnaden ligger vid hamnen och är en av stadens moderna landmärken.'),
('Bodö', 'Bodins kyrka', 'Medeltida kyrka', 'Bodin kyrka är en stenkyrka från omkring 1240 och en av regionens äldsta byggnader. Den ligger strax utanför centrum och har en stämningsfull interiör.'),
('Bodö', 'Keiservarden', 'Utsiktsberg ovanför staden', 'Keiservarden är ett berg på 339 meter med utsikt över Bodö, havet och Lofoten. Berget fick sitt namn efter kejsar Wilhelm II, som besökte platsen 1889.'),
('Bodö', 'Bodö domkyrka', 'Stadens domkyrka', 'Bodö domkyrka byggdes efter andra världskriget, då staden bombades 1940. Kyrkan är en modern byggnad och domkyrka för Sør-Hålogaland.'),
('Bodö', 'Aspmyra stadion', 'Fotbollsarena för Bodø/Glimt', 'Aspmyra stadion är hemmaplan för fotbollslaget Bodø/Glimt. Arenan ligger norr om polcirkeln och har blivit känd efter lagets framgångar i europeiska cuper.'),
('Bodö', 'Hurtigrutenkajen', 'Kaj för Hurtigruten', 'Hurtigrutenkajen är en anhalt för Hurtigruten, kustlinjen som har trafikerat den norska kusten sedan 1893. Härifrån kan man resa söderut eller norrut längs kusten.'),
-- Lillehammer
('Lillehammer', 'Maihaugen', 'Friluftsmuseum', 'Maihaugen grundades 1904 av Anders Sandvig och är ett av Nordens största friluftsmuseer med över 200 byggnader. Här finns bland annat en stavkyrka från Garmo och en stadsmiljö från 1900-talet.'),
('Lillehammer', 'Lysgårdsbackarna', 'Hoppbackar från OS 1994', 'Lysgårdsbakkene är hoppbackar som användes vid de olympiska vinterspelen 1994. I dag används backarna för tävlingar och träning, och man kan åka upp och se utsikten.'),
('Lillehammer', 'Olympiaparken', 'Olympisk park', 'Olympiaparken har anläggningar från OS 1994, bland annat Håkons Hall, Kristins Hall och Birkebeineren. Här finns också ett olympiskt museum och aktiviteter för besökare.'),
('Lillehammer', 'Hunderfossen familjepark', 'Familjepark', 'Hunderfossen familjepark ligger cirka 15 kilometer norr om Lillehammer och har sagomotiv, troll och attraktioner för barn. Parken är byggd runt norska sagor och är populär bland familjer.'),
('Lillehammer', 'Lillehammers konstmuseum', 'Konstmuseum', 'Lillehammer Kunstmuseum visar norsk konst från 1800-talet till i dag. Museet har samlingar av norska målare och temporära utställningar.'),
('Lillehammer', 'Nordseter', 'Fjällområde med skidåkning', 'Nordseter är ett fjällområde ovanför Lillehammer med skidspår, vandringsleder och utsikt över Mjösa. Området används året om för friluftsliv.'),
('Lillehammer', 'Mjösa', 'Norges största sjö', 'Mjösa är Norges största sjö, cirka 117 kilometer lång. Den är omgiven av jordbruksbygd, och man kan åka ångbåt över sjön eller ta en promenad längs stranden.'),
('Lillehammer', 'Skibladner', 'Världens äldsta hjulångare i trafik', 'Skibladner är en hjulångare från 1856 som trafikerar Mjösa. Den är världens äldsta hjulångare som fortfarande går i reguljär trafik, och en tur ger en nostalgisk upplevelse.'),
('Lillehammer', 'Hafjell', 'Skidort', 'Hafjell ligger cirka 15 kilometer från Lillehammer och var en alpin anläggning vid OS 1994. Här finns skidbackar på vintern och en av Norges främsta bikeparker på sommaren.'),
('Lillehammer', 'Gågatan', 'Shoppinggata i centrum', 'Storgata är Lillehammers gågata och har många butiker, kaféer och restauranger i färgglada trähus. Den är stadens mest livliga stråk.'),
-- Alta
('Alta', 'Hällristningarna i Alta (UNESCO)', 'Hällristningar på Unescos världsarvslista', 'Hällristningarna i Alta finns på Unescos världsarvslista sedan 1985. De är mellan 2 000 och 6 000 år gamla och visar jakt, djur och människor. Man kan följa en spång genom området vid Hjemmeluft.'),
('Alta', 'Altamuseet', 'Museum om hällristningar', 'Altamuseet vid hällristningarna belönades 1993 som Årets europeiska museum. Utställningarna berättar om hällristningarna, samiska traditioner och regionens historia.'),
('Alta', 'Norrskenskatedralen', 'Kyrka med särpräglad arkitektur', 'Norrskenskatedralen öppnade 2013 och ritades av Schmidt Hammer Lassen. Byggnaden har en spiralformad form täckt av titan och är en symbol för Alta och norrskenet.'),
('Alta', 'Altaelva', 'Älv med laxfiske', 'Altaelva är en älv som är känd för sitt laxfiske. Den rinner genom Alta och hade en stor politisk betydelse under Altakonflikten på 1970-talet.'),
('Alta', 'Finnmarksløpet', 'Hundspannstävling', 'Finnmarksløpet är Europas nordligaste och längsta hundspannstävling, cirka 1 000 kilometer. Den startar i Alta varje mars och är en stor händelse i staden.'),
('Alta', 'Altacanyon', 'Kanjon', 'Altacanyon, Sautso, är en av Nordeuropas största kanjoner, upp till cirka 400 meter djup. Man kan göra en båttur eller vandra till kanten för utsikt.'),
('Alta', 'Sorrisniva Igloo Hotel', 'Hotell av is', 'Sorrisniva Igloo Hotel byggs av is och snö varje vinter vid Altaelva. Gästerna kan sova i isrum, äta i isbaren och uppleva norrskenet.'),
('Alta', 'Halddeobservatoriet', 'Världens första norrskensobservatorium', 'Halddeobservatoriet byggdes 1899 på berget Haldde ovanför Alta för att studera norrsken. Fysikern Kristian Birkeland forskade här. Man kan vandra upp till ruinerna och får utsikt över Altafjorden.'),
('Alta', 'Komsa', 'Fornlämning och berg', 'Komsa är ett berg vid Altafjorden som har gett namn åt Komsakulturen, en stenåldersbosättning från omkring 10 000 år sedan. Man kan vandra upp till området och få utsikt över fjorden.'),
('Alta', 'Altafjorden', 'Fjord', 'Altafjorden skär in i Finnmark och är en vacker plats för naturupplevelser. Under andra världskriget gömde sig det tyska slagskeppet Tirpitz här.'),

-- ---------------- DANMARK ----------------
-- Köpenhamn
('Köpenhamn', 'Tivoli', 'Nöjespark i centrala Köpenhamn', 'Tivoli öppnade 1843 och är en av världens äldsta nöjesparker. Här finns karuseller, trädgårdar, konserter och fyrverkerier, och den träbaserade berg-och-dalbanan Rutschebanen från 1914 är fortfarande i drift.'),
('Köpenhamn', 'Nyhavn', 'Färgglada hus längs kanalen', 'Nyhavn är en hamn och kanal från 1670-talet kantad av färgglada hus. H.C. Andersen bodde här en tid. I dag finns restauranger, barer och gamla träbåtar längs kajen.'),
('Köpenhamn', 'Lilla sjöjungfrun', 'Bronsstaty vid Langelinie', 'Statyn av Lilla sjöjungfrun vid Langelinie skapades av Edvard Eriksen och invigdes 1913. Den bygger på H.C. Andersens saga och bekostades av Carl Jacobsen från Carlsberg.'),
('Köpenhamn', 'Amalienborg', 'Kungafamiljens palats', 'Amalienborg består av fyra identiska rokokopalats kring ett åttkantigt torg. Palatsen byggdes på 1700-talet och är kungafamiljens residens. Vaktavlösningen sker varje dag.'),
('Köpenhamn', 'Rosenborg slott', 'Renässansslott med kronjuveler', 'Rosenborg slott byggdes 1606–1634 av Christian IV och ligger vid Kongens Have. Här förvaras de danska kronjuvelerna och kungliga skatter, och slottet är ett populärt besöksmål.'),
('Köpenhamn', 'Christiansborg', 'Danmarks parlament', 'Christiansborg rymmer Folketinget, statsministerns kontor och Högsta domstolen. Slottets torn är cirka 106 meter högt och är Köpenhamns högsta, med utsikt över staden.'),
('Köpenhamn', 'Strøget', 'Köpenhamns gågata', 'Strøget är en av Europas längsta gågator, cirka en kilometer lång. Den har varit bilfri sedan 1962 och är full av butiker, kaféer och gatuartister.'),
('Köpenhamn', 'Christiania', 'Alternativt område med egen kultur', 'Christiania grundades 1971 i ett tidigare militärområde och är ett självstyrt område med alternativ kultur, konst och kreativa miljöer. Fotografering är förbjuden vid Pusher Street.'),
('Köpenhamn', 'Rundetårn (Runda tornet)', 'Torn med spiralformad rampgång', 'Runda tornet byggdes 1642 av Christian IV och har en spiralformad rampgång i stället för trappor. Uppe i tornet finns ett observatorium och utsikt över Köpenhamns tak.'),
('Köpenhamn', 'Nationalmuseet', 'Danmarks största kulturhistoriska museum', 'Nationalmuseet visar Danmarks historia från förhistoria och vikingatid till modern tid, samt samlingar från hela världen. Här finns bland annat föremål från bronsåldern och vikingatiden.'),
-- Århus
('Århus', 'Den Gamle By (Gamla staden)', 'Friluftsmuseum med danska stadsmiljöer', 'Den Gamle By öppnade 1909 och var världens första friluftsmuseum för stadsbebyggelse. Här finns omkring 75 gamla hus från hela Danmark, och man kan uppleva stadsliv från 1500-talet till 1970-talet.'),
('Århus', 'ARoS', 'Konstmuseum med regnbågspanorama', 'ARoS Aarhus Kunstmuseum öppnade 2004 och har nio våningar inspirerade av Dantes Inferno. På taket finns Olafur Eliassons regnbågspanorama, Your rainbow panorama, med utsikt över staden.'),
('Århus', 'Moesgaard Museum', 'Arkeologi och etnografi', 'Moesgaard Museum ligger utanför Århus i en byggnad från 2014 med gräsklätt tak. Museet visar bland annat Grauballemannen, en välbevarad mosselik från järnåldern, och berättar om människans historia.'),
('Århus', 'Latinerkvarteret (Latinkvarteret)', 'Gamla kvarter med kaféer', 'Latinerkvarteret är Århus äldsta stadsdel med smala gator, kaféer, butiker och gamla hus. Här ligger Møllestien, en av Danmarks vackraste gator med rosenklädda hus.'),
('Århus', 'Tivoli Friheden', 'Nöjespark', 'Tivoli Friheden är en nöjespark i Århus som öppnade 1903. Här finns karuseller, berg-och-dalbanor och familjeaktiviteter, och parken är öppen under sommarsäsongen.'),
('Århus', 'Århus domkyrka', 'Danmarks längsta kyrka', 'Århus domkyrka, Sankt Clemens, är Danmarks längsta och högsta kyrka, med ett torn på cirka 96 meter. Kyrkan har anor från 1100-talet och är känd för sina kalkmålningar.'),
('Århus', 'Marselisborg', 'Kungligt sommarslott', 'Marselisborg slott är kungafamiljens sommarresidens och ligger i en stor park söder om Århus. Slottet skänktes 1902 till kronprins Christian och parken är öppen för besökare.'),
('Århus', 'Dokk1', 'Bibliotek och kulturhus vid hamnen', 'Dokk1 öppnade 2015 och är ett av Nordens största bibliotek och kulturhus. Byggnaden ligger vid hamnen och har utsikt över Århusbukten.'),
('Århus', 'Botaniska trädgården', 'Park med växthus', 'Århus botaniska trädgård anlades 1875 och har växthus, planteringar och gamla träd. Den är en lugn och skön plats mitt i staden.'),
('Århus', 'Infinite Bridge', 'Cirkelformad gångbro i havet', 'Infinite Bridge är en cirkelformad gångbro på cirka 60 meter i diameter som sticker ut i havet. Bron har ritats av arkitekter och är ett populärt mål för en promenad vid vattnet.'),
-- Odense
('Odense', 'H.C. Andersens hus', 'Museum om H.C. Andersen', 'Museet i Odense berättar om H.C. Andersens liv och sagor. Det nya huset öppnade 2021 och har utställningar och en sagoträdgård som ger en inblick i sagoförfattarens värld.'),
('Odense', 'Fynske byn', 'Friluftsmuseum', 'Den Fynske Landsby är ett friluftsmuseum med omkring 25 byggnader från 1700- och 1800-talen. Här kan man se hur byfolket levde och arbetade på Fyn förr.'),
('Odense', 'Odense djurpark', 'Zoo', 'Odense Zoo har många djur från hela världen och är en av Danmarks större djurparker. Här finns afrikanska och asiatiska djur, och parken är populär bland barnfamiljer.'),
('Odense', 'Brandts', 'Kulturcentrum', 'Brandts är ett kulturcentrum i den gamla Brandts Klædefabrik och rymmer museum för konst och visuell kultur, fotografi och mediehistoria.'),
('Odense', 'Møntergården', 'Stadsmuseum', 'Møntergården är Odenses stadsmuseum och berättar om stadens historia från vikingatiden till i dag. Här finns även ett stort mynt- och medaljkabinett.'),
('Odense', 'Sankt Knuds kyrka', 'Gotisk domkyrka', 'Sankt Knuds kyrka är Odenses domkyrka, uppförd i gotisk stil på 1200-talet. Den är uppkallad efter Knud den helige, som mördades 1086, och hans reliker vilar i kyrkan.'),
('Odense', 'Munke Mølle', 'Vattenkvarn', 'Munke Mølle är en vattenkvarn vid Odense å med anor från 1200-talet. Den ligger i en vacker park och är en populär plats för promenader.'),
('Odense', 'Fyns konstmuseum', 'Konstmuseum', 'Fyns Kunstmuseum grundades 1885 och visar dansk konst från 1700-talet till i dag. Samlingarna innehåller verk av guldålderns målare och modern konst.'),
('Odense', 'Carl Nielsen-museet', 'Museum om tonsättaren Carl Nielsen', 'Carl Nielsen-museet berättar om Danmarks mest kända tonsättare, född på Fyn. Museet visar föremål, instrument och manuskript och ger en inblick i hans liv och musik.'),
('Odense', 'Egeskov slott (i närheten)', 'Vattenslott med park', 'Egeskov slott på södra Fyn byggdes 1554 på ekpålar i en sjö och är Europas bäst bevarade renässansvattenslott. Slottet har en stor park, labyrint och flera museer.'),
-- Ålborg
('Ålborg', 'Jomfru Ane Gade (Jomfru Ane-gatan)', 'Utegata med barer och klubbar', 'Jomfru Ane Gade är Ålborgs berömda uteslivsgata. Här ligger barer, restauranger och klubbar tätt, och gatan är full av folk på helgkvällar.'),
('Ålborg', 'Ålborg Zoo', 'Zoo', 'Ålborg Zoo ligger i en skogsklädd park och visar djur från hela världen. Här finns både stora och små djur, och parken passar för hela familjen.'),
('Ålborg', 'Utzon Center', 'Museum om arkitektur och design', 'Utzon Center ritades av Jørn Utzon, känd för Sydneys operahus, och öppnade 2008. Centret visar arkitektur, design och konst och ligger vackert vid fjorden.'),
('Ålborg', 'Kunsten', 'Museum för modern konst', 'Kunsten Museum of Modern Art är ritat av Alvar Aalto och Elissa Aalto och öppnade 1972. Museet visar dansk och internationell modern konst i en byggnad av vit marmor.'),
('Ålborg', 'Ålborgtornet', 'Utsiktstorn', 'Ålborgtornet byggdes 1933 och är cirka 105 meter högt. Från toppen har man utsikt över Ålborg, Limfjorden och omgivningarna.'),
('Ålborg', 'Nordkraft', 'Kulturcentrum i före detta kraftverk', 'Nordkraft är ett kulturcentrum i ett gammalt kraftverk. Här finns bio, teater, konsertsal och gym, och byggnaden är ett landmärke i stadens industrihistoria.'),
('Ålborg', 'Budolfi kyrka', 'Stadens domkyrka', 'Budolfi kyrka är Ålborgs domkyrka och uppkallad efter Sankt Botolph. Kyrkan är från 1400-talet och ligger i stadens centrum.'),
('Ålborg', 'Ålborgs slott', 'Renässansslott', 'Ålborgs slott, Aalborghus, byggdes 1539 för kung Christian III. Slottet ligger vid fjorden och är numera statligt ämbetsverk, men man kan promenera runt området.'),
('Ålborg', 'Lindholm Høje', 'Vikingagravfält', 'Lindholm Høje är ett stort gravfält från vikingatiden och järnåldern med omkring 700 gravar, många markerade med stenar. Här finns också ett museum som berättar om området.'),
('Ålborg', 'Jens Bangs stenhus', 'Historisk handelsgård från 1624', 'Jens Bangs Stenhus byggdes 1624 för köpmannen Jens Bang och är Nordjyllands största bevarade renässanshus. Det är en av stadens mest kända byggnader och är rikt dekorerat.'),
-- Esbjerg
('Esbjerg', 'Fanø', 'Ö med långa sandstränder', 'Fanø är en ö utanför Esbjerg som nås med färja på cirka tolv minuter. Den är känd för sina långa sandstränder, där man kan köra bil, och charmiga byar som Sønderho.'),
('Esbjerg', 'Människan vid havet', 'Fyra vita statyer vid havet', 'Mennesket ved Havet är fyra vita statyer, nio meter höga, som sitter vid havet. De skapades av Svend Wiig Hansen och invigdes 1995, och är Esbjergs symbol.'),
('Esbjerg', 'Fiskeri- och sjöfartsmuseet', 'Museum med akvarium', 'Fiskeri- og Søfartsmuseet har akvarier, ett sälarium och utställningar om danskt fiske och sjöfart. Museet passar bra för familjer med barn.'),
('Esbjerg', 'Vattentornet', 'Vattentorn från 1897', 'Vandtårnet byggdes 1897 och är ett landmärke i Esbjerg. Tornet är cirka 55 meter högt och man kan klättra upp för en utsikt över staden.'),
('Esbjerg', 'Esbjergs konstmuseum', 'Konstmuseum', 'Esbjerg Kunstmuseum visar modern och samtida dansk konst. Museet har skiftande utställningar och en fin samling av 1900-talets konst.'),
('Esbjerg', 'Vadehavscentret', 'Besökscenter om Vadehavet', 'Vadehavscentret ligger nära Ribe och berättar om Vadehavet, ett Unesco-världsarv. På våren kan man se fenomenet svart sol, när miljontals stare flyger i täta moln.'),
('Esbjerg', 'Mandø', 'Ö i Vadehavet', 'Mandø är en liten ö i Vadehavet som nås vid lågvatten, och man kan ta Mandøbussen, en traktorbuss. Ön har få invånare och är känd för sin natur och sitt fågelliv.'),
('Esbjerg', 'Marbæk plantage', 'Skog vid havet', 'Marbæk plantage är en skog vid havet med promenadstigar och cykelleder. Området är lugnt och passar bra för en utflykt nära Esbjerg.'),
('Esbjerg', 'Sønderho', 'Charmig by på Fanø', 'Sønderho är en välbevarad by på södra Fanø med halmtäckta hus och en gammal kyrka. Byn ger en bild av hur livet var förr på ön.'),
('Esbjerg', 'Esbjerg hamn', 'Stor hamn', 'Esbjerg hamn är en av Danmarks största fiskehamnar och en viktig bas för havsbaserad vindkraft och oljeindustri. Härifrån går även färja till Fanø.'),
-- Roskilde
('Roskilde', 'Roskilde domkyrka', 'Världsarvskyrka med kungagravar', 'Roskilde domkyrka påbörjades på 1170-talet och är Unesco-världsarv sedan 1995. Här vilar omkring 40 danska kungar och drottningar, och kyrkan är ett centrum för dansk historia.'),
('Roskilde', 'Vikingaskeppsmuseet', 'Museum med vikingaskepp', 'Vikingaskeppsmuseet visar fem vikingaskepp som sänktes vid Skuldelev omkring år 1070 och grävdes upp 1962. Man kan även se båtbyggare arbeta i museets verkstad.'),
('Roskilde', 'Roskilde Festival', 'Stor musikfestival', 'Roskilde Festival har ordnats sedan 1971 och är en av Europas största musikfestivaler. Den hålls i början av juli och drar omkring 130 000 besökare.'),
('Roskilde', 'Ragnarock', 'Rockmuseum', 'Ragnarock är ett museum för pop, rock och ungdomskultur som öppnade 2016. Byggnaden ligger vid hamnen och har utställningar om musikhistoria och ungdomsrörelser.'),
('Roskilde', 'Museet för samtidskonst', 'Samtidskonst', 'Museet för samtidskonst visar nutida konst av danska och internationella konstnärer i centrala Roskilde. Utställningarna växlar och är ofta experimentella.'),
('Roskilde', 'Land of Legends (Lejre)', 'Historisk upplevelsepark', 'Land of Legends i Lejre är en upplevelsepark som visar hur människor levde under stenåldern, järnåldern och vikingatiden. Besökare kan prova på forntida hantverk.'),
('Roskilde', 'Roskildefjorden', 'Fjord', 'Roskildefjorden är en cirka 40 kilometer lång fjord som skär in i Själland. Den är en bra plats för båtliv, vandring och naturupplevelser och har rik fågelfauna.'),
('Roskilde', 'Sankt Jørgensbjerg', 'Gammal by', 'Sankt Jørgensbjerg är Roskildes äldsta stadsdel med gamla hus, smala gator och en medeltida kyrka. Området ger en stämningsfull bild av hur staden såg ut förr.'),
('Roskilde', 'Kongehøj', 'Bronsåldershög vid Bognæs', 'Kongehøj är en gravhög från bronsåldern vid grusvägen mot halvön Bognæs i Skjoldungernes Land, en nationalpark vid Roskildefjorden. Här har man hittat ett skelett i stenkista, ett bronssvärd och två bronsknivar. Från trakten har man utsikt över fjorden.'),
('Roskilde', 'Gågatorna', 'Shoppinggator', 'Roskildes gågator, bland annat Skomagergade och Algade, har butiker, kaféer och restauranger. Området ligger nära domkyrkan och är en trevlig plats för en promenad.'),
-- Helsingör
('Helsingör', 'Kronborg', 'Hamlets slott', 'Kronborg är ett renässansslott från 1420-talet som är Unesco-världsarv sedan 2000. Shakespeare gjorde det känt som Hamlets Elsinore. I källaren sitter statyn av Holger Danske, enligt sägnen redo att vakna om Danmark hotas.'),
('Helsingör', 'M/S Sjöfartsmuseet', 'Sjöfartsmuseum', 'M/S Museet for Søfart är Danmarks nationella sjöfartsmuseum och ligger i en torrdocka vid Kronborg. Byggnaden ritades av BIG och öppnade 2013. Utställningarna berättar om dansk sjöfart.'),
('Helsingör', 'Sankt Olai kyrka', 'Stadens domkyrka', 'Sankt Olai kyrka är Helsingörs domkyrka, uppförd i gotisk stil på 1400-talet. Den är uppkallad efter Olav den helige och ligger mitt i gamla stan.'),
('Helsingör', 'Karmeliterklostret', 'Medeltida kloster', 'Karmeliterklostret i Helsingör, Sct. Mariæ Kirke, är ett av Nordeuropas bäst bevarade medeltida klosterkomplex. Det grundades på 1400-talet och har en vacker klostergård.'),
('Helsingör', 'Kulturværftet', 'Kulturhus i före detta varv', 'Kulturværftet är ett kulturhus som öppnade 2010 i ett gammalt varv. Här finns teater, konserter, bibliotek och café med utsikt över sundet.'),
('Helsingör', 'Marienlyst strand', 'Badstrand', 'Marienlyst strand är en populär badstrand i Helsingör med fin utsikt över Öresund och Sverige. Intill ligger Marienlyst slott och ett spa- och kasinohotell.'),
('Helsingör', 'Gamla stan', 'Historisk stadskärna', 'Helsingörs gamla stad har smala gränder, välbevarade hus från 1500- och 1600-talen och livliga gator som Stengade. Området är en bra plats för en promenad.'),
('Helsingör', 'Fredensborg slott', 'Kungligt slott', 'Fredensborg slott byggdes på 1720-talet av Frederik IV och är kungafamiljens vår- och höstresidens. Slottet ligger vid Esrum sjö och har en stor park.'),
('Helsingör', 'Gilleleje', 'Fiskeby', 'Gilleleje är en pittoresk fiskeby på Själlands nordkust med hamn, fiskrestauranger och kullerstensgator. Här gömdes judar i kyrkan 1943 innan de rymde till Sverige.'),
('Helsingör', 'Louisiana (Humlebæk)', 'Konstmuseum', 'Louisiana i Humlebæk, cirka 20 kilometer söder om Helsingör, är ett av världens mest kända museer för modern konst. Det öppnade 1958 och har en skulpturpark vid Öresund med verk av bland andra Giacometti.'),
-- Ribe
('Ribe', 'Ribe domkyrka', 'Medeltida domkyrka', 'Ribe domkyrka är en av Danmarks äldsta och påbörjades på 1100-talet. Man kan klättra upp i tornet, 248 trappsteg, och få en vacker utsikt över staden och Vadehavet.'),
('Ribe', 'Ribe Vikingecenter', 'Vikingatida upplevelsecenter', 'Ribe Vikingecenter är ett friluftsområde med återuppbyggda hus från vikingatiden. Här kan man se hantverk, prova på traditionella aktiviteter och få en känsla för hur vikingarna levde.'),
('Ribe', 'Ribe konstmuseum', 'Konstmuseum', 'Ribe Kunstmuseum grundades 1891 och är Danmarks äldsta konstmuseum. Samlingarna visar dansk konst, bland annat från guldåldern, i en vacker miljö.'),
('Ribe', 'Ribes gamla stad', 'Medeltida stadskärna', 'Ribe är Danmarks äldsta stad med rötter i 700-talet. Gamla stan har kullerstensgator, korsvirkeshus och gamla gränder som ger en känsla av medeltid.'),
('Ribe', 'Sankta Katarina kyrka och kloster', 'Medeltida kyrka och kloster', 'Sct. Catharinæ Kirke och Kloster är ett dominikanerkloster grundat 1228. Kyrkan och klosterbyggnaderna är välbevarade och hör till stadens äldsta byggnader.'),
('Ribe', 'Vadehavscentret', 'Besökscenter om Vadehavet', 'Vadehavscentret berättar om Vadehavets natur och djurliv. Här kan man lära sig om tidvattnet och fåglarna, och på våren se spektakulära stare-svärmar.'),
('Ribe', 'Mandø', 'Ö i Vadehavet', 'Mandø är en liten ö i Vadehavet som nås vid lågvatten med Mandøbussen. Ön har få invånare och är känd för sin natur och sitt fågelliv.'),
('Ribe', 'Ribe å', 'Å genom staden', 'Ribe å rinner genom staden och var tidigare en viktig vattenväg. I dag är ån och dess omgivningar en lugn plats för promenader och avkoppling.'),
('Ribe', 'Ribes vikingamuseum', 'Museum om vikingatiden', 'Ribes Vikinger är ett museum som öppnade 2017 och berättar om vikingatidens Ribe, Skandinaviens äldsta handelsplats. Utställningarna bygger på arkeologiska fynd från området.'),
('Ribe', 'Slusen', 'Sluss med märken för stormfloder', 'Slusen i Ribe skyddar staden mot högvatten från Vadehavet. Här finns märken som visar hur högt vattnet nådde vid tidigare stormfloder, bland annat vid den stora floden 1634.'),
-- Billund
('Billund', 'Legoland', 'Nöjespark byggd av Lego', 'Legoland Billund öppnade 1968 och är den första av Legolands nöjesparker. I Miniland finns modeller byggda av miljontals klossar, och parken har attraktioner för hela familjen. Det är Danmarks största turistmål utanför Köpenhamn.'),
('Billund', 'LEGO House', 'Upplevelsehus om Lego', 'LEGO House öppnade 2017 och ritades av BIG. Huset är cirka 12 000 kvadratmeter och har lekzoner, utställningar och Masterpiece Gallery med fantastiska Lego-skulpturer.'),
('Billund', 'Lalandia', 'Vattenland och semesterpark', 'Lalandia Billund är en semesterpark med tropiskt vattenland, shopping och aktiviteter för barn. Den ligger nära Legoland och passar bra för en regnig dag.'),
('Billund', 'Givskud Zoo', 'Safaripark', 'Givskud Zoo grundades 1969 som en lejonsafari och har vuxit till en stor djurpark. Man kan åka bil genom lejonens område och se många andra djur på nära håll.'),
('Billund', 'Jelling (UNESCO)', 'Runstenar och kungshögar', 'Jelling är Unesco-världsarv sedan 1994 och har två kungshögar, en kyrka och de berömda runstenarna från 900-talet. Den större runstenen har kallats Danmarks dopattest.'),
('Billund', 'Egtved', 'Känd för Egtvedflickan', 'Egtved är känt för Egtvedflickan, en bronsåldersgrav från omkring 1370 f.Kr. som hittades 1921. Graven har blivit en symbol för bronsåldern i Danmark, och en kopia finns på platsen.'),
('Billund', 'Vejlefjorden', 'Fjord', 'Vejlefjorden är en fjord nära Billund med vacker natur, stränder och skogsområden. Staden Vejle ligger vid fjordens inre del.'),
('Billund', 'Billunds flygplatsutställning', 'Lego-installation på flygplatsen', 'LEGO House och Billunds flygplats har skapat en permanent installation med tre Lego-modeller i ankomsthallen. Figurerna är inspirerade av skulpturer i LEGO House och hälsar resenärer välkomna.'),
('Billund', 'Lego Inside Tour', 'Guidad Lego-tur', 'Lego Inside Tour är en guidad rundtur som tar besökare bakom kulisserna i Legos hemstad. Man får lära sig mer om företagets historia, hur klossarna skapas och vilken betydelse leken har.'),
('Billund', 'Sculpturepark Billund', 'Skulpturpark mellan Legoland och centrum', 'Skulpturparken är en cirka 1,3 kilometer lång stig med omkring tjugo skulpturer längs Billund å. Den ligger mellan nöjesområdet och centrum och är en lugn paus från Legoland. Parken är gratis och öppen året om.'),
-- Skagen
('Skagen', 'Grenen', 'Danmarks nordspets där två hav möts', 'Grenen är den nordligaste punkten i Danmark, där Skagerrak och Kattegatt möts. Man kan gå ut på den smala sandtungan och stå med en fot i varje hav. Traktorbussen Sandormen kör från parkeringen.'),
('Skagen', 'Råbjerg Mile', 'Vandrande sanddyn', 'Råbjerg Mile är en av Nordeuropas största vandrande sanddyner, cirka 40 meter hög. Dynen rör sig österut med omkring 15 meter per år och erbjuder en öken-liknande miljö.'),
('Skagen', 'Skagens museum', 'Museum för Skagenmålarna', 'Skagens Museum öppnade 1908 och visar konst av Skagenmålarna, bland andra P.S. Krøyer och Anna och Michael Ancher. De fångade ljuset och människorna i Skagen.'),
('Skagen', 'Den igensandade kyrkan', 'Kyrktorn i sanden', 'Den Tilsandede Kirke var en medeltida kyrka som övergavs 1795 när flygsanden hotade att begrava den. I dag finns bara tornet kvar och det är ett av Skagens mest kända landmärken.'),
('Skagen', 'Skagens fyr', 'Fyr med utsikt', 'Skagen Fyr, Det grå fyr, byggdes 1858 och är cirka 46 meter högt. Man kan klättra upp och få utsikt över Skagen, hav och dyner.'),
('Skagen', 'Skagens hamn', 'Hamn med fiskrestauranger', 'Skagens hamn är en av Danmarks största fiskehamnar och en livlig plats med fiskebåtar, fiskauktioner och restauranger. Här kan man äta färsk fisk och skaldjur.'),
('Skagen', 'Anchers hus', 'Konstnärshem', 'Anchers Hus var konstnärerna Michael och Anna Anchers hem och är sedan 1967 museum. Här kan man se hur paret bodde och arbetade, och många av deras målningar.'),
('Skagen', 'Skagen Odde naturcenter', 'Naturcenter', 'Skagen Odde Naturcenter berättar om naturen vid Danmarks nordspets och ligger vid Grenen. Utställningar och aktiviteter visar djur, växter och landskapets historia.'),
('Skagen', 'Bunkermuseet', 'Museum i tyska bunkrar', 'Bunkermuseet i Skagen ligger i tyska bunkrar från andra världskriget, en del av Atlantvallen. Utställningarna visar hur Skagen påverkades av kriget.'),
('Skagen', 'Skagens strand', 'Lång sandstrand', 'Skagens strand är en lång, bred sandstrand med klart vatten. Den är populär för bad, promenader och för att se solnedgången över havet.');

UPDATE places p
    JOIN cities c ON c.id = p.city_id
    JOIN place_texts t ON t.city_name = c.name AND t.place_name = p.name
SET p.short_description = t.short_description,
    p.description = t.description;

DROP TEMPORARY TABLE place_texts;