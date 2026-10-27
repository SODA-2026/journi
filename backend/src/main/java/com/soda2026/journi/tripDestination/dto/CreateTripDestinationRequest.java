package com.soda2026.journi.tripDestination.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateTripDestinationRequest(
        @NotNull
        @Positive
        Long sourceSuggestionId,

        @NotNull
        @PositiveOrZero
        Integer orderIndex
) {
}

/*
 * SV — Indata för att lägga till ett katalogförslag i en resa
 * sourceSuggestionId är obligatoriskt och positivt; orderIndex är obligatoriskt och icke-negativt.
 * tripId kommer från URL:en. Land och platsnamn hämtas av tjänsten från katalogen.
 * Klienten skickar inga egna destinationsnamn. Record skapar konstruktor och läsmetoder.
 *
 * RU — Запрос на добавление направления из каталога в поездку
 * sourceSuggestionId обязателен и положителен; orderIndex обязателен и неотрицателен.
 * tripId берётся из URL. Страну и место сервис получает из каталога.
 * Клиент не передаёт собственные названия направлений. Record создаёт конструктор и методы чтения.
 */
