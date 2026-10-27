package com.soda2026.journi.tripMember.dto;

import com.soda2026.journi.tripMember.TripMemberRole;
import jakarta.validation.constraints.NotNull;

public record UpdateTripMemberRoleRequest(
        @NotNull TripMemberRole role
) {
}

/*
 * Begäran om rolländring
 * Endast EDITOR och VIEWER accepteras. Rollen måste anges.
 * Ägarens behörighet kontrolleras i tjänsten.
 *
 * Запрос изменения роли
 * Допустимы только EDITOR и VIEWER. Роль обязательна.
 * Право владельца изменять роль проверяется в сервисе.
 */
