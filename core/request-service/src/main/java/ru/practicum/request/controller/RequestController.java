package ru.practicum.request.controller;

import interaction.model.request.ParticipationRequestDtoOut;
import interaction.model.request.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequestDtoOut> getUserRequests(@PathVariable Long userId) {
        log.info("Received GET request for requests of user with id: {}", userId);

        List<ParticipationRequestDtoOut> requests = requestService.findByUserId(userId);
        log.info("Returning {} requests for user with id: {}", requests.size(), userId);

        return requests;
    }

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDtoOut createRequest(@PathVariable Long userId,
                                                    @RequestParam Long eventId) {
        log.info("Received POST request to create request for userId: {}, eventId: {}", userId, eventId);

        ParticipationRequestDtoOut request = requestService.create(userId, eventId);
        log.info("Created request with id: {} for userId: {}, eventId: {}", request.getId(), userId, eventId);

        return request;
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDtoOut cancelRequest(@PathVariable Long userId,
                                                    @PathVariable Long requestId) {
        log.info("Received PATCH request to cancel request with id: {} for userId: {}", requestId, userId);

        ParticipationRequestDtoOut request = requestService.cancel(userId, requestId);
        log.info("Cancelled request with id: {} for userId: {}", request.getId(), userId);

        return request;
    }

    @GetMapping("/requests/{id}")
    ParticipationRequestDtoOut getById(@PathVariable("id") Long id) {
        log.info("Запрос на получение запроса с ID = {} от микросервиса", id);
        return requestService.getById(id);
    }

    @GetMapping("/requests/by-ids")
    public List<ParticipationRequestDtoOut> getByIds(@RequestParam List<Long> requestIds) {
        log.info("Запрос на получение запросов с ID = {} от микросервиса", requestIds);
        return requestService.getByIds(requestIds);
    }

    @GetMapping("/requests/by-event-id")
    public List<ParticipationRequestDtoOut> getByEventId(@RequestParam Long eventId) {
        log.info("Запрос от микросервиса на получение запросов события с ID = {}", eventId);
        return requestService.getByEventId(eventId);
    }

    @PutMapping("/requests/set-status")
    public void setStatusRequests(@RequestParam List<Long> requestIds,
                                 @RequestParam Status status) {
        log.info("Запрос от микросервиса на установку статуса {} для запросов с ID = {}", status, requestIds);
        requestService.setStatusRequests(requestIds, status);
    }

    @GetMapping("/requests/count-by-event-id")
    List<Object[]> getCountRequestByEventId(@RequestParam List<Long> eventIds,
                                            @RequestParam Status status) {
        log.info("Запрос от микросервиса на получение кол-ва запросов к событию с ID = {}", eventIds);
        return requestService.getCountRequestByEventId(eventIds, status);
    }
}