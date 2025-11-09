package ru.practicum.events.service;

import interaction.model.event.in.EventRequestStatusUpdateRequest;
import interaction.model.event.in.NewEventDto;
import interaction.model.event.in.UpdateEventAdminRequest;
import interaction.model.event.in.UpdateEventUserRequest;
import interaction.model.event.output.EventFullDto;
import interaction.model.event.output.EventShortDto;
import interaction.model.event.output.SwitchRequestsStatus;
import interaction.model.request.ParticipationRequestDtoOut;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.events.model.EventAdminParam;
import ru.practicum.events.model.EventPublicParam;

import java.util.List;

public interface EventService {

    EventFullDto updateEvent(UpdateEventAdminRequest request, Long eventId);

    List<EventFullDto> findEvents(EventAdminParam param);

    EventFullDto getEvent(Long eventId);

    List<EventShortDto> findEvents(EventPublicParam param);

    SwitchRequestsStatus switchRequestsStatus(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, Long eventId, Long userId);

    List<ParticipationRequestDtoOut> getRequests(Long userId, Long eventId);

    EventFullDto updateEvent(UpdateEventUserRequest updateEventUserRequest, Long eventId, Long userId);

    EventFullDto getEvent(Long eventId, Long userId);

    EventFullDto createEvent(NewEventDto newEventDto, Long userId);

    List<EventShortDto> getEventsForUser(Long userId, Integer from, Integer to);

    EventFullDto getEventFullDtoById(Long eventId);

    EventShortDto getEventShortDtoById(Long eventId);

    List<EventShortDto> getByIds(List<Long> eventIds);
}