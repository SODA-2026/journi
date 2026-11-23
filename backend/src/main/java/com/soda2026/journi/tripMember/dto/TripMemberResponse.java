 package com.soda2026.journi.tripMember.dto;

 import com.soda2026.journi.tripMember.TripMemberModel;

 public record TripMemberResponse(
         Long tripId,
         Long userId,
         String name
 ) {

     public static TripMemberResponse from(TripMemberModel member) {
         return new TripMemberResponse(
                 member.getId().getTripId(),
                 member.getId().getUserId(),
                 member.getUser().getName()
         );
     }
 }

 /*
  * Medlemsuppgifter till klienten
  * Returnerar resans id, användarens id.
  * Inkluderar deltagarens namn för medlemslistan.
  *
  * Данные участника для клиента
  * Возвращает ID поездки, ID пользователя.
  * Включает имя участника для списка участников.
  */
