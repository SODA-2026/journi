# Skapa en resa lokalt utan inloggning

Du behöver Java 25, Node.js/npm och en startad MySQL-databas.
Skapa `backend/.env` med egna databasinställningar enligt `backend/.env.exemple`.
Lägg inte till `.env` i Git.

## Starta backend

Kör i projektets rot (mappen med `backend` och `frontend`):

```bash
bash demo.sh
```

Skriptet hittar Java 25 automatiskt på macOS. På andra system ska `JAVA_HOME`
peka på Java 25. Profilen gäller bara denna körning.
Vänta på `Started JourniApplication` och lämna terminalen öppen.
Om port 8080 är upptagen, stoppa tidigare backend i IDEA eller med Ctrl+C först.

## Starta frontend

Öppna en annan terminal i projektets rot:

```bash
cd frontend
npm install
npm run dev
```

`npm install` behövs första gången och när beroendena ändras.

## Skapa en resa

Öppna http://localhost:5173. Den gula rutan **Demo: inloggning är avstängd.**
visar att backend kör i demoläge. Fyll i land, stad och datum, klicka **Skapa resa**.
Vid lyckat sparande visas **Resan är skapad!** och resans id.
Alla resor sparas i MySQL med samma testanvändare `trip-demo@journi.invalid`.
Detta testar skapande av resor, inte inloggning eller behörigheter.

## Stoppa och felsök

Stoppa båda servrarna med Ctrl+C i respektive terminal.
Om backend inte är tillgänglig, kontrollera fel i backend-terminalen.
Om du ombeds logga in, kontrollera att backend startats med `bash demo.sh`
och att `local-demo` visas som aktiv profil i loggen.

Om du tidigare körde `export SPRING_PROFILES_ACTIVE=local-demo` direkt i terminalen,
kör `unset SPRING_PROFILES_ACTIVE` innan du startar backend normalt.

Demoprofilen används bara lokalt och binder backend till `127.0.0.1`.
Den skapar testanvändaren vid behov och ger en testidentitet endast för anonym
POST till `/api/trips` eller `/trips`. Utan profilen krävs riktig autentisering
(anonym begäran får 401), och demorutan visas inte.
