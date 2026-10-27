package com.soda2026.journi.tripMember;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripMemberRepository extends JpaRepository<TripMemberModel, TripMemberId> {

    List<TripMemberModel> findAllById_TripIdOrderById_UserIdAsc(Long tripId);
}

/*
 * Databasåtkomst
 * JpaRepository använder TripMemberId som nyckeltyp i findById och existsById.
 * Den egna metoden hämtar resans medlemmar sorterade efter användar-id.
 * Id_TripId och Id_UserId betyder id.tripId respektive id.userId i modellen.
 *
 * Доступ к базе
 * JpaRepository использует TripMemberId как тип ключа в findById и existsById.
 * Дополнительный метод получает участников поездки по возрастанию ID пользователя.
 * Id_TripId и Id_UserId означают id.tripId и id.userId в модели.
 */
