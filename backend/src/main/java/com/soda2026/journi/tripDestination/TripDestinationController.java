package com.soda2026.journi.tripDestination;

import com.soda2026.journi.tripDestination.dto.CreateTripDestinationRequest;
import com.soda2026.journi.tripDestination.dto.TripDestinationResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trips/{tripId}/destinations")
public class TripDestinationController {

    private final TripDestinationService service;

    public TripDestinationController(TripDestinationService service) {
        this.service = service;
    }

    @GetMapping
    public List<TripDestinationResponse> getDestinations(
            @PathVariable("tripId") @Positive Long tripId
    ) {
        return service.getDestinations(tripId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripDestinationResponse createDestination(
            @PathVariable("tripId") @Positive Long tripId,
            @Valid @RequestBody CreateTripDestinationRequest request
    ) {
        // Временно: пользователь 1, как в создающем поездку TripController Сэма.
        // TODO после подключения входа:
        // 1. Добавить java.security.Principal principal в параметры этого метода.
        // 2. Включить перегрузку с Principal в TripDestinationService (ниже в его файле).
        // 3. Заменить следующий return на:
        // return service.createDestination(tripId, request, principal);
        // ID пользователя не принимать из JSON или URL.
        return service.createDestination(tripId, request, 1L);
    }
}

/*
 HTTP-gränssnitt
 * Basadress: /api/trips/{tripId}/destinations. Spring injicerar tjänsten via konstruktorn.
 * GET returnerar resans destinationer. POST tar emot JSON och returnerar 201 Created.
 * @PathVariable läser resans id från adressen; @Positive kräver ett positivt id.
 * @RequestBody läser JSON; @Valid aktiverar valideringen i begärans DTO.
 * Affärslogiken ligger i tjänsten; kontrollern anropar inte repositoryt direkt.
 * TODO: Slutför tjänstens kontroller innan API:et används i produktion.
 *
 * HTTP-интерфейс
 * Базовый адрес: /api/trips/{tripId}/destinations. Spring передаёт сервис через конструктор.
 * GET возвращает остановки поездки. POST принимает JSON и возвращает 201 Created.
 * @PathVariable получает id поездки из адреса; @Positive требует положительный id.
 * @RequestBody читает JSON; @Valid запускает проверки DTO запроса.
 * Бизнес-логика находится в сервисе; контроллер не обращается к репозиторию напрямую.
 * TODO: Завершить проверки сервиса перед использованием API в production.
 */
