package com.soda2026.journi.tripDestination;

import com.soda2026.journi.tripDestination.dto.CreateTripDestinationRequest;
import com.soda2026.journi.tripDestination.dto.TripDestinationResponse;
import com.soda2026.journi.trip.TripModel;
import com.soda2026.journi.suggestion.SuggestionModel;
import com.soda2026.journi.suggestion.DestinationKind;
import com.soda2026.journi.suggestion.SuggestionType;
import com.soda2026.journi.user.UserModel;
import com.soda2026.journi.tripMember.TripMemberId;
import com.soda2026.journi.tripMember.TripMemberModel;
import com.soda2026.journi.tripMember.TripMemberRole;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

@Service
@Validated
public class TripDestinationService {
    private final TripDestinationRepository repository;
    private final EntityManager entityManager;
    private final TripDestinationMapper mapper;

    public TripDestinationService(
            TripDestinationRepository repository,
            EntityManager entityManager,
            TripDestinationMapper mapper
    ) {
        this.repository = repository;
        this.entityManager = entityManager;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<TripDestinationResponse> getDestinations(Long tripId) {
        return repository.findAllByTrip_IdOrderByOrderIndexAsc(tripId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    /*
     * TODO после подключения входа: включить эту перегрузку и переключить контроллер
     * с 1L на Principal. Пример предполагает email в principal.getName(); это нужно
     * согласовать с реализацией входа. Проверки владельца/EDITOR остаются в методе с userId.
     * Отсутствующий Principal должен давать 401, без подстановки пользователя 1.
     *
    @Transactional
    public TripDestinationResponse createDestination(
            @NotNull @Positive Long tripId,
            @NotNull @Valid CreateTripDestinationRequest request,
            java.security.Principal principal
    ) {
        if (principal == null || principal.getName() == null
                || principal.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        UserModel user = entityManager.createQuery(
                        "select u from UserModel u where u.email = :email", UserModel.class)
                .setParameter("email", principal.getName())
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        return createDestination(tripId, request, user.getId());
    }
     */

    @Transactional
    public TripDestinationResponse createDestination(
            @NotNull @Positive Long tripId,
            @NotNull @Valid CreateTripDestinationRequest request,
            @NotNull @Positive Long userId
    ) {
        UserModel user = entityManager.find(UserModel.class, userId);
        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found"
            );
        }

        TripModel trip = entityManager.find(TripModel.class, tripId);
        if (trip == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found");
        }

        requireCanEdit(trip, user);
        return createDestination(tripId, request);
    }

    @Transactional
    TripDestinationResponse createDestination(
            @NotNull @Positive Long tripId,
            @NotNull @Valid CreateTripDestinationRequest request
    ) {
        TripModel trip = entityManager.find(TripModel.class, tripId);
        if (trip == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found");
        }
        if (repository.existsByTrip_IdAndOrderIndex(tripId, request.orderIndex())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Destination order index is already occupied"
            );
        }
        SuggestionModel sourceSuggestion = entityManager.find(
                SuggestionModel.class, request.sourceSuggestionId()
        );
        if (sourceSuggestion == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Suggestion not found");
        }
        if (!"DESTINATION".equals(String.valueOf(sourceSuggestion.getType()))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Suggestion must be a destination"
            );
        }

        String country = sourceSuggestion.getCountry();
        if (country == null || country.isBlank() || country.length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, "Suggestion has an invalid country"
            );
        }
        country = country.strip();

        String placeName = null;
        if (!"COUNTRY".equals(String.valueOf(sourceSuggestion.getDestinationKind()))) {
            placeName = normalizePlaceName(sourceSuggestion.getName());
            if (placeName == null || placeName.length() > 150) {
                throw new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY, "Suggestion has an invalid place name"
                );
            }
        }

        TripDestinationModel destination = new TripDestinationModel(
                trip,
                sourceSuggestion,
                country,
                placeName,
                request.orderIndex()
        );

        TripDestinationModel savedDestination = repository.save(destination);
        return mapper.toDto(savedDestination);
    }

    @Transactional
    public TripDestinationResponse createFirstDestination(
            @NotNull TripModel trip,
            @NotNull @Positive Long countryId,
            @NotNull @Positive Long cityId
    ) {
        if (trip.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Trip must be saved before adding a destination"
            );
        }
        SuggestionModel country = entityManager.find(
                SuggestionModel.class, countryId
        );
        if (country == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Country not found"
            );
        }

        SuggestionModel city = entityManager.find(
                SuggestionModel.class, cityId
        );
        if (city == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "City not found"
            );
        }

        if (country.getType() != SuggestionType.DESTINATION
                || country.getDestinationKind() != DestinationKind.COUNTRY) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selected country must be a country destination"
            );
        }

        if (city.getType() != SuggestionType.DESTINATION
                || city.getDestinationKind() != DestinationKind.CITY) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selected city must be a city destination"
            );
        }

        if (city.getParentSuggestion() == null
                || !countryId.equals(city.getParentSuggestion().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selected city does not belong to selected country"
            );
        }

        return createDestination(
                trip.getId(),
                new CreateTripDestinationRequest(cityId, 0)
        );
    }


    private void requireCanEdit(TripModel trip, UserModel user) {
        if (Objects.equals(trip.getOwnerId(), user.getId())) {
            return;
        }

        TripMemberModel member = entityManager.find(
                TripMemberModel.class,
                new TripMemberId(trip.getId(), user.getId())
        );

        if (member == null || member.getRole() != TripMemberRole.EDITOR) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only the trip owner or an editor may add destinations"
            );
        }
    }

    private String normalizePlaceName(String placeName) {
        if (placeName == null) {
            return null;
        }

        String normalized = placeName.strip();
        return normalized.isEmpty() ? null : normalized;
    }

}

/*
 * Tjänstens ansvar
 * Spring injicerar repositoryt och EntityManager. @Validated aktiverar argumentvalidering.
 * getDestinations läser sorterade destinationer och skapar DTO:er i en lästransaktion.
 * En resa utan destinationer eller en obefintlig resa ger en tom lista.
 * createDestination laddar resan och katalogförslaget; saknade referenser ger 404.
 * Endast förslag av typen DESTINATION accepteras; andra typer ger 400.
 * Land kopieras från förslaget. För COUNTRY lämnas platsnamnet tomt;
 * för CITY, REGION och PLACE kopieras förslagets namn till placeName.
 * Ogiltiga kataloguppgifter ger 422. Destinationen sparas inom samma transaktion.
 * normalizePlaceName tar bort omgivande blanksteg och ersätter tomma namn med null.
 * Förutsätter getType(), getDestinationKind(), getCountry() och getName() i SuggestionModel.
 * POST använder tills vidare användare 1; endast ägaren eller EDITOR får lägga till.
 * createFirstDestination anropas av TripService efter dess användarkontroll i samma transaktion.
 *
 * Назначение сервиса
 * Spring передаёт репозиторий и EntityManager. @Validated включает проверку аргументов.
 * getDestinations читает остановки по порядку и создаёт DTO в транзакции чтения.
 * Для поездки без остановок или несуществующей поездки возвращает пустой список.
 * createDestination загружает поездку и предложение каталога; отсутствие записи даёт 404.
 * Принимает только предложения типа DESTINATION; другой тип даёт 400.
 * Страна копируется из предложения. Для COUNTRY место остаётся null;
 * для CITY, REGION и PLACE в placeName копируется название предложения.
 * Некорректные данные каталога дают 422. Сохранение выполняется в одной транзакции.
 * normalizePlaceName убирает пробелы по краям и заменяет пустое название на null.
 * Ожидает getType(), getDestinationKind(), getCountry() и getName() в SuggestionModel.
 * POST временно использует пользователя 1; добавление разрешено владельцу или EDITOR.
 * createFirstDestination вызывает TripService после проверки пользователя в общей транзакции.
 */
