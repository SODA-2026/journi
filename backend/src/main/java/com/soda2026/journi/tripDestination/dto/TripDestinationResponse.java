package com.soda2026.journi.tripDestination.dto;

public record TripDestinationResponse(
        Long id,
        Long tripId,
        Long sourceSuggestionId,
        String country,
        String placeName,
        Integer orderIndex
) {
}

/*
 * Utdata till klienten
 * Returnerar destinationens id, kopplade id:n och ruttuppgifter utan att exponera JPA-entiteten.
 * TripDestinationMapper.toDto omvandlar modellen till en DTO. En saknad sourceSuggestion blir null.
 * TripModel och SuggestionModel förutsätts ha getId().
 * Omvandlingen görs i tjänstens transaktion så att LAZY-relationer kan användas.
 *
 * Данные ответа клиенту
 * Возвращает id остановки, id связанных объектов и данные маршрута, не передавая JPA-сущность.
 * TripDestinationMapper.toDto преобразует модель в DTO. Если sourceSuggestion отсутствует, возвращается null.
 * Предполагается наличие getId() в TripModel и SuggestionModel.
 * Преобразование выполняется внутри транзакции сервиса для работы с LAZY-связями.
 */
