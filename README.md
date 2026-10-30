<!--
  INSTRUKTION
  Det här är projektets startsida. Ersätt allt i den här filen med information om ert projekt.
  Kommentarer som den här syns bara när man redigerar filen, inte på GitHub. Ta bort dem när ni är klara.
-->

# Journi

Journi är en webbapplikation för personer som planerar resor tillsammans. Användare kan skapa resor, lägga till destinationer och aktiviteter samt samarbeta med andra resenärer i samma reseplan.

## Teamet

| Namn | GitHub |
|---|---|
| Dhannea | [GitHub-profil](https://github.com/dhannea-pearl) |
| Olena | [GitHub-profil](https://github.com/olenavitkovska86-ops) |
| Sam | [GitHub-profil](https://github.com/samshaalan) |
| Anders | [GitHub-profil](https://github.com/Andoorz) |
| Arvid | [GitHub-profil](https://github.com/arvideriksson28-hub) |


## Teknik

<!-- Frontend, backend, databas och eventuella större bibliotek. Motivera valet i docs/beslutslogg.md. -->

| Del | Teknik |
|---|---|
| Frontend | React, TypeScript, Tailwind och Vite |
| Backend | Java 25 och Spring Boot |
| Databas | MySQL med Spring Data JPA och Flyway |

## Kom igång

### Förutsättningar

- Java 25
- Maven
- Node.js och npm
- MySQL

### Klona projektet

```bash
git clone <repo-url>
cd journi
```
#### Starta databasen
Skapa en MySQL-databas och konfigurera miljövariablerna i backend/.env:

```bash
DB_URL=jdbc:mysql://localhost:3306/<databasnamn>
DB_USERNAME=<användarnamn>
DB_PASSWORD=<lösenord>
```

#### Starta backend  
Från projektets rotmapp:

```bash
cd backend
./mvnw spring-boot:run
```

På Windows används:

```bash
mvnw.cmd spring-boot:run
```

#### Starta frontend  
Öppna en ny terminal:
```bash
cd frontend
npm install
npm run dev
```

Frontend startas därefter av Vite på den lokala adress som visas i terminalen.

## Dokumentation

| Dokument | Innehåll |
|---|---|
| [Gruppkontrakt](docs/gruppkontrakt.md) | Hur vi samarbetar |
| [Definition of Done](docs/definition-of-done.md) | När en story räknas som klar |
| [MVP](docs/mvp.md) | Vad som ingår i första versionen |
| [Databasschema](docs/databasschema.md) | Tabeller och relationer |
| [Beslutslogg](docs/beslutslogg.md) | Viktiga beslut och varför |
| [Sprintar](docs/sprintar/) | Planning, standups, review och retro per sprint |
| [Individuella reflektioner](docs/reflektioner/) | En per person, lämnas in vecka 8 |

## Board

<!-- Länk till ert projekt i GitHub Projects. -->

[Länk till boarden](https://andershansson91.atlassian.net/jira/software/projects/KAN/boards/2?filter=&groupBy=none&atlOrigin=eyJpIjoiMmVkMDdhYTQ0MmM4NDhiMmFhZDE2ZThiODQzNGRlNDkiLCJwIjoiaiJ9)
