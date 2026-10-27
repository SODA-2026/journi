package com.soda2026.journi.tripMember;

import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.user.UserModel;
import com.soda2026.journi.tripMember.dto.TripMemberResponse;
import com.soda2026.journi.tripMember.dto.UpdateTripMemberRoleRequest;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Service
@Validated
public class TripMemberService {

    private final TripMemberRepository repository;
    private final EntityManager entityManager;

    public TripMemberService(TripMemberRepository repository, EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public List<TripMemberResponse> getMembers(
            @NotNull @Positive Long tripId,
            Principal principal
    ) {
        UserModel requester = getCurrentUser(principal);
        TripModel trip = getTrip(tripId);
        if (!Objects.equals(trip.getOwnerId(), requester.getId())
                && !repository.existsById(new TripMemberId(tripId, requester.getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Trip access denied");
        }

        return repository.findAllById_TripIdOrderById_UserIdAsc(tripId)
                .stream()
                .map(TripMemberResponse::from)
                .toList();
    }

    @Transactional
    public TripMemberResponse updateRole(
            @NotNull @Positive Long tripId,
            @NotNull @Positive Long userId,
            @NotNull @Valid UpdateTripMemberRoleRequest request,
            Principal principal
    ) {
        UserModel requester = getCurrentUser(principal);
        TripModel trip = getTrip(tripId);
        requireOwner(trip, requester);
        requireNonOwnerMember(trip, userId);

        TripMemberModel member = getMember(tripId, userId);
        member.setRole(request.role());
        return TripMemberResponse.from(member);
    }

    @Transactional
    public void removeMember(
            @NotNull @Positive Long tripId,
            @NotNull @Positive Long userId,
            Principal principal
    ) {
        UserModel requester = getCurrentUser(principal);
        TripModel trip = getTrip(tripId);
        requireOwner(trip, requester);
        requireNonOwnerMember(trip, userId);
        repository.delete(getMember(tripId, userId));
    }

    private UserModel getCurrentUser(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        return entityManager.createQuery(
                        "select u from UserModel u where u.email = :email", UserModel.class
                )
                .setParameter("email", principal.getName())
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Authenticated user not found"
                ));
    }

    private TripModel getTrip(Long tripId) {
        TripModel trip = entityManager.find(TripModel.class, tripId);
        if (trip == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found");
        }
        return trip;
    }

    private TripMemberModel getMember(Long tripId, Long userId) {
        return repository.findById(new TripMemberId(tripId, userId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Trip member not found"
                ));
    }

    private void requireOwner(TripModel trip, UserModel requester) {
        if (!Objects.equals(trip.getOwnerId(), requester.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the trip owner may manage members");
        }
    }

    private void requireNonOwnerMember(TripModel trip, Long userId) {
        if (Objects.equals(trip.getOwnerId(), userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The owner is not a membership role");
        }
    }
}

/*
 * Hantering av befintliga medlemmar
 * Ägare och medlemmar kan läsa listan; endast ägaren kan ändra roller och ta bort medlemmar.
 * Ägaren hämtas från trips.owner_id och hanteras inte som EDITOR eller VIEWER.
 * Rolländringar sparas automatiskt för den laddade entiteten inom transaktionen.
 * Integrationskrav: autentiseringen måste leverera användarens e-post i Principal.getName().
 * Utan Principal returneras 401. Ingen identitet hämtas från JSON eller URL-parametrar.
 * TODO: Anpassa getCurrentUser till teamets slutliga autentiseringslösning.
 * Nya medlemskap ska skapas av inbjudningstjänsten när en inbjudan accepteras,
 * i samma transaktion som statusändringen. Det finns ingen offentlig metod för att gå med direkt.
 *
 * Управление существующими участниками
 * Владелец и участники могут читать список; только владелец меняет роли и удаляет участников.
 * Владелец берётся из trips.owner_id и не является EDITOR или VIEWER.
 * Изменение роли загруженной сущности сохраняется автоматически внутри транзакции.
 * Условие интеграции: авторизация должна передавать email пользователя в Principal.getName().
 * Без Principal возвращается 401. Личность вызывающего не берётся из JSON или URL.
 * TODO: Адаптировать getCurrentUser к итоговой реализации авторизации команды.
 * Нового участника создаёт сервис приглашений при принятии приглашения,
 * в одной транзакции со сменой его статуса. Публичного метода прямого вступления нет.
 */
