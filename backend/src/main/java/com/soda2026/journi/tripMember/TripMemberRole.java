package com.soda2026.journi.tripMember;

public enum TripMemberRole {
    EDITOR,
    VIEWER
}

/*
 * Medlemsroller
 * EDITOR kan redigera resans innehåll. VIEWER kan läsa och rösta.
 * Roller ger inte behörighet automatiskt; tjänsterna måste kontrollera dem.
 * Ägaren bestäms av trips.owner_id och är inte en medlemsroll.
 *
 * Роли участников
 * EDITOR может редактировать содержимое поездки. VIEWER может читать и голосовать.
 * Enum сам не предоставляет доступ; роли должны проверяться в сервисах.
 * Владелец определяется по trips.owner_id и не является ролью участника.
 */
