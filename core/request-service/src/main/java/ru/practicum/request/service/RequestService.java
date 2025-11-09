package ru.practicum.request.service;


import interaction.model.request.ParticipationRequestDtoOut;
import interaction.model.request.Status;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDtoOut> findByUserId(Long userId);

    ParticipationRequestDtoOut create(Long userId, Long eventId);

    ParticipationRequestDtoOut cancel(Long userId, Long requestId);

    ParticipationRequestDtoOut getById(Long id);

    List<ParticipationRequestDtoOut> getByIds(List<Long> requestIds);

    List<ParticipationRequestDtoOut> getByEventId(Long eventId);

    void setStatusRequests(List<Long> requestIds, Status status);

    List<Object[]> getCountRequestByEventId(List<Long> eventIds, Status status);
}
