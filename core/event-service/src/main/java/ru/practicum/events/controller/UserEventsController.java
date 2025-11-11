package ru.practicum.events.controller;

import interaction.model.request.ParticipationRequestDtoOut;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import interaction.model.event.in.EventRequestStatusUpdateRequest;
import interaction.model.event.in.NewEventDto;
import interaction.model.event.in.UpdateEventUserRequest;
import interaction.model.event.output.EventFullDto;
import interaction.model.event.output.EventShortDto;
import interaction.model.event.output.SwitchRequestsStatus;
import ru.practicum.events.service.EventServiceImpl;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/events")
public class UserEventsController {
    private final EventServiceImpl eventService;

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public SwitchRequestsStatus switchRequestsStatus(@PathVariable Long eventId,
                                                     @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
                                                     @PathVariable Long userId) {
        return eventService.switchRequestsStatus(eventRequestStatusUpdateRequest, eventId, userId);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDtoOut> getRequests(@PathVariable Long eventId, @PathVariable Long userId) {
        return eventService.getRequests(userId, eventId);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@PathVariable Long eventId, @PathVariable Long userId) {
        return eventService.getEvent(eventId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                             @RequestParam(defaultValue = "10") @Min(0) Integer to) {
        return eventService.getEventsForUser(userId, from, to);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@RequestBody @Valid NewEventDto newEventDto, @PathVariable Long userId) {
        return eventService.createEvent(newEventDto, userId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest,
                                    @PathVariable Long userId) {
        return eventService.updateEvent(updateEventUserRequest, eventId, userId);
    }
}
