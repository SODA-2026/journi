package com.soda2026.journi.tripMember;

import com.soda2026.journi.tripMember.dto.TripMemberResponse;
import com.soda2026.journi.tripMember.dto.UpdateTripMemberRoleRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/trips/{tripId}/members")
public class TripMemberController {

    private final TripMemberService service;

    public TripMemberController(TripMemberService service) {
        this.service = service;
    }

    @GetMapping
    public List<TripMemberResponse> getMembers(
            @PathVariable("tripId") @Positive Long tripId,
            Principal principal
    ) {
        return service.getMembers(tripId, principal);
    }

    @PatchMapping("/{userId}")
    public TripMemberResponse updateRole(
            @PathVariable("tripId") @Positive Long tripId,
            @PathVariable("userId") @Positive Long userId,
            @Valid @RequestBody UpdateTripMemberRoleRequest request,
            Principal principal
    ) {
        return service.updateRole(tripId, userId, request, principal);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(
            @PathVariable("tripId") @Positive Long tripId,
            @PathVariable("userId") @Positive Long userId,
            Principal principal
    ) {
        service.removeMember(tripId, userId, principal);
    }
}

/*
 * API för resans medlemmar
 * GET /api/trips/{tripId}/members returnerar listan över medlemmar.
 * PATCH /api/trips/{tripId}/members/{userId} ändrar rollen med JSON: {"role":"VIEWER"}.
 * DELETE på samma medlemsadress tar bort medlemskapet och returnerar 204.
 * Principal kommer från serverns autentisering; tjänsten kontrollerar behörigheten.
 * POST saknas avsiktligt: medlemskap skapas efter accepterad inbjudan.
 *
 * API участников поездки
 * GET /api/trips/{tripId}/members возвращает список участников.
 * PATCH /api/trips/{tripId}/members/{userId} меняет роль через JSON: {"role":"VIEWER"}.
 * DELETE по тому же адресу удаляет участие и возвращает 204.
 * Principal берётся из авторизации сервера; права проверяет сервис.
 * POST не добавлен: участник создаётся после принятия приглашения.
 */
