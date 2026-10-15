# Beslutslogg

<!--
  INSTRUKTION
  Här skriver ni ner större beslut och varför ni tog dem. Det är inte en lista över allt ni gör,
  utan över de val som påverkar projektet och som någon senare kan undra över.

  Exempel på beslut som ska hit:
  - Val av stack, ramverk eller större bibliotek
  - Hur ni strukturerar koden (mappar, lager, API-design)
  - Större ändringar i MVP:n eller databasschemat
  - Saker ni medvetet valt bort

  Skriv beslutet när det tas, inte i efterhand. Lägg nyaste beslutet överst.
  Ett beslut kan ersättas av ett senare. Skriv då "Ersatt av #N" på det gamla i stället för att ta bort det.
-->

## Mall

Kopiera den här för varje nytt beslut:

```markdown
## #N Rubrik

**Datum:**
**Status:** Gäller / Ersatt av #N

**Bakgrund:** Vad behövde vi bestämma, och varför?

**Alternativ:**
- Alternativ A
- Alternativ B

**Beslut:** Vad valde vi?

**Varför:** Varför valde vi det, och vad valde vi bort?

**Konsekvenser:** Vad betyder beslutet för projektet framåt?
```

---

> **Exempel (Sysslo):**
>
> ## #2 Ingen inloggning i MVP:n
>
> **Datum:** 2026-11-04
> **Status:** Gäller
>
> **Bakgrund:** Inloggning stod i första utkastet av MVP:n. När vi estimerade blev den en L, och vi har inte byggt inloggning förut.
>
> **Alternativ:**
> - Bygga egen inloggning med lösenord
> - Använda en färdig tjänst (t.ex. Auth0)
> - Ingen inloggning i MVP:n. Man väljer vem man är från en lista i hushållet.
>
> **Beslut:** Ingen inloggning i MVP:n.
>
> **Varför:** Inloggning är inte kärnan i appen och skulle ta en hel sprint. Vi vill hellre visa fungerande sysslor tidigt. Användartestet visade också att ingen ville skapa konto.
>
> **Konsekvenser:** Vem som helst med länken kan se hushållet. Vi tar upp inloggning igen efter MVP:n (Should i backloggen).
>
> ## #1 Stack: React, Node/Express och PostgreSQL
>
> **Datum:** 2026-10-28
> **Status:** Gäller
>
> **Bakgrund:** Vi behövde välja teknik innan vi satte upp projektet.
>
> **Alternativ:**
> - React + Node/Express + PostgreSQL
> - Angular + Spring Boot + PostgreSQL
>
> **Beslut:** React, Node/Express och PostgreSQL.
>
> **Varför:** Tre av fyra i gruppen har jobbat med React och Node. Samma språk i frontend och backend gör det lättare att hjälpa varandra.
>
> **Konsekvenser:** Jonas, som mest kan Java, behöver lära sig Express. Vi parprogrammerar första sprinten.

---

<!-- Era beslut nedan, nyaste överst. -->
