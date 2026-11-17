 package com.soda2026.journi.tripMember.dto;

 import com.soda2026.journi.tripMember.TripMemberModel;

 public record TripMemberResponse(
         Long tripId,
         Long userId
 ) {

     public static TripMemberResponse from(TripMemberModel member) {
         return new TripMemberResponse(
                 member.getId().getTripId(),
                 member.getId().getUserId()
         );
     }
 }

 /*
  * Medlemsuppgifter till klienten
  * Returnerar resans id, användarens id.
  * from använder den sammansatta nyckeln utan att ladda användarens eller resans uppgifter.
  *
  * Данные участника для клиента
  * Возвращает ID поездки, ID пользователя.
  * from читает составной ключ без загрузки данных пользователя или поездки.
  */
