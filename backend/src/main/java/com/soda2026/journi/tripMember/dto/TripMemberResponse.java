package com.soda2026.journi.tripMember.dto;

import com.soda2026.journi.tripMember.TripMemberModel;
import com.soda2026.journi.tripMember.TripMemberRole;

public record TripMemberResponse(
        Long tripId,
        Long userId,
        TripMemberRole role
) {

    public static TripMemberResponse from(TripMemberModel member) {
        return new TripMemberResponse(
                member.getId().getTripId(),
                member.getId().getUserId(),
                member.getRole()
        );
    }
}

/*
 * Medlemsuppgifter till klienten
 * Returnerar resans id, användarens id och medlemsrollen.
 * from använder den sammansatta nyckeln utan att ladda användarens eller resans uppgifter.
 *
 * Данные участника для клиента
 * Возвращает ID поездки, ID пользователя и роль участника.
 * from читает составной ключ без загрузки данных пользователя или поездки.
 */
