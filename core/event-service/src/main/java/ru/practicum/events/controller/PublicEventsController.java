package ru.practicum.events.controller;

import interaction.model.event.output.EventFullDto;
import interaction.model.event.output.EventShortDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.CollectorClient;
import ru.practicum.events.model.EventPublicParam;
import ru.practicum.events.service.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
@Slf4j
public class PublicEventsController {

    private final EventService eventService;
    private final CollectorClient collectorClient;


    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable Long eventId, @RequestHeader("X-EWM-USER-ID") Long userId) {
        EventFullDto eventFullDto = eventService.getEvent(eventId);

        collectorClient.saveView(userId, eventId);
        log.info("User action of view event with id: {} received to Stats-Server", eventId);

        return eventFullDto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getPublicEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Set<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false) Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            HttpServletRequest request) {

        EventPublicParam param = new EventPublicParam(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventService.findEvents(param);
    }

    @GetMapping("/full-event-by-id")
    public EventFullDto getEventFullDtoById(@RequestParam Long eventId) {
        log.info("Запрос от микросервиса request-service события с ID = {}", eventId);
        return eventService.getEventFullDtoById(eventId);
    }

    @GetMapping("/short-event-by-id")
    public EventShortDto getEventShortDtoById(@RequestParam Long eventId) {
        log.info("Запрос от микросервиса comment-service события с ID = {}", eventId);
        return eventService.getEventShortDtoById(eventId);
    }

    @GetMapping("/by-ids")
    public List<EventShortDto> getByIds(@RequestParam List<Long> eventIds) {
        log.info("Запрос микросервисом событий с ID {}", eventIds);
        return eventService.getByIds(eventIds);
    }

    @GetMapping("/recommendations")
    public List<EventShortDto> getRecommendations(@RequestHeader("X-EWM-USER-ID") Long userId) {
        log.info("Запрос  подходящих событий для пользователя с ID {}", userId);
        return eventService.getRecommendationsForUser(userId);
    }

    @PutMapping("/{eventId}/like")
    public void likeEvent(@PathVariable Long eventId, @RequestHeader("X-EWM-USER-ID") Long userId) {
        eventService.likeEvent(eventId, userId);
    }
}
