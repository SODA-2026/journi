package com.soda2026.journi.tripDestination;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TripDestinationRepository
       extends JpaRepository<TripDestinationModel, Long> {

    List<TripDestinationModel> findAllByTrip_IdOrderByOrderIndexAsc(
            Long tripId
    );

}

/*
 *  Databasåtkomst
 * JpaRepository ger standardmetoder som save och findById.
 * findAllByTrip_IdOrderByOrderIndexAsc hämtar resans destinationer i stigande ordning.
 * Spring Data skapar frågan från metodnamnet: Trip_Id betyder trip.id,
 * och OrderByOrderIndexAsc anger sorteringen. Utan träffar returneras en tom lista.
 *
 * Доступ к базе
 * JpaRepository предоставляет стандартные методы, например save и findById.
 * findAllByTrip_IdOrderByOrderIndexAsc получает остановки поездки по возрастанию позиции.
 * Spring Data строит запрос по имени метода: Trip_Id означает trip.id,
 * а OrderByOrderIndexAsc задаёт сортировку. При отсутствии записей возвращается пустой список.
 */
