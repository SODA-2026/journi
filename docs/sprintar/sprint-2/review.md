# Sprint review: sprint N

**Datum:**
*2026-11-09*  
**Deltagare:**
*Anders, Dhannea, Arvid, Sam, Olena*

## Del 1: Förberedelse

### Sprintmål

Som användare kan man logga in och se sina resor på frontend samt lägga till aktiviteter/resor och redigera resa.


**Uppnåddes sprintmålet?** Delvis

### Blev klart


| Issue | Story | Visas av |
|---|---|---|
| KAN-82 | Lägga till routing | Arvid |
| KAN-48 | Som inloggad användare vill jag se en lista över mina sparade reseplaner så att jag kan hitta och öppna dem igen | Arvid |
| KAN-80 | Välj autentiseringsstrategi – JWT eller Session | Olena |
| KAN-73 | Task: branch protection: main branch bara mergas från main-dev | Anders |
| KAN-83 | Se över databas med team |  |
| KAN-85 | Ändra frontend filstruktur till feature based |  |

### Blev inte klart


| Issue | Story | Varför inte klar | Vad händer med den? |
|---|---|---|---|
| KAN-29 | Som användare vill jag kunna lägga till flera destinationer på en resa så att jag kan planera en rundresa | db-problem |  |
| KAN-42 | Som användare vill jag kunna registrera mig, logga in och ut för att säkert komma åt mitt konto och mina resor | db-planering kom ivägen | flyttas till nästa sprint |
| KAN-27 | Som användare vill jag kunna ändra information om en befintlig resa så att jag kan uppdatera mina planer | db-planering kom ivägen | flyttas till nästa sprint |
| KAN-30 | Som användare vill jag kunna lägga till aktiviteter med datum, tid och beskrivning så att jag kan planera vad jag ska göra under resan | db-planering kom ivägen | flyttas till nästa sprint |
| KAN-28 | Som användare vill jag kunna ta bort en resa som jag inte längre behöver så att min reseöversikt hålls aktuell | db-planering kom ivägen | flyttas till nästa sprint |
| KAN-84 | Som användare vill jag se detaljerna för en resa så att jag har överblick över planen | db-planering kom ivägen | flyttas till nästa sprint |
| KAN-31 | Som användare vill jag kunna skriva anteckningar om resan så att jag kan samla viktig information på ett ställe | db-planering kom ivägen | flyttas till nästa sprint |
| KAN-33 | Som användare vill jag se dagens program och närmaste aktivitet så att jag vet vad som händer nu | db-planering kom ivägen | flyttas till nästa sprint |


### Demoplan

1. Sam visar resorna som är tillagda och klickar på knapparna för att demonstrera routing
2. Arvid plockar fram db ifall kund är intresserad
3.

### Frågor till läraren

- Ska vi alltid ha review 13.00?
- fokus på synligt arbete eller funktioner i programmet?

## Del 2: Efter reviewn

### Feedback

> **Exempel:** Läraren frågade hur man vet vem som ska göra nästa disk. I deras kollektiv turas man om varje vecka i stället för att tilldela.

- inlogg är bra att repetera hur man bygger
- ge ett riktigt intryck i slutet av projektet, visuellt
- vill ha fler möjligheter till att skapa resa
- se mer av den visuella resan
- liten grej, förslah på matställen, museeum och liknande
- kalendervy visuellt
- inspirationsflöde, ex. i lund finns detta matstället eller denna platsen att besöka. kunna välja en plats och gå vidare.
- jaga low hanging fruit, småsaker som ger värde
- interaktiv karta?
- side note - password reset token i db, behövs den?

### Ändringar i backloggen

<!-- Nya stories, ändrad prioritet, stories som tagits bort. Länka issues. -->

> **Exempel:** Ny story #15 "Turordning per vecka" som Must. "Tilldela en syssla" (#5) sänks till Should.

- KAN-84 lades till, öppna ett kort med detaljerad info om en resa
