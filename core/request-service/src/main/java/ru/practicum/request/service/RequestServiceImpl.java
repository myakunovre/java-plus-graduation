package ru.practicum.request.service;

import interaction.client.EventFeignClient;
import interaction.client.UserFeignClient;
import interaction.exceptions.ConflictException;
import interaction.exceptions.NotFoundException;
import interaction.model.event.State;
import interaction.model.event.output.EventFullDto;
import interaction.model.request.ParticipationRequestDtoOut;
import interaction.model.request.Status;
import interaction.model.user.output.UserShortDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventFeignClient eventClient;
    private final UserFeignClient userClient;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDtoOut> findByUserId(Long userId) {
        log.info("Fetching requests for user with id: {}", userId);

        findUserById(userId);

        List<ParticipationRequestDtoOut> requests = requestRepository.findByRequesterId(userId).stream()
                .map(requestMapper::toParticipationRequestDtoOut)
                .toList();

        log.info("Found {} requests for user with id: {}", requests.size(), userId);
        return requests;
    }


    @Override
    public ParticipationRequestDtoOut create(Long userId, Long eventId) {
        log.info("Creating request for user with id: {} and event with id: {}", userId, eventId);

        UserShortDto requester = findUserById(userId);
        EventFullDto event = eventClient.getEventFullDtoById(eventId);

        validateRequestCreation(requester, event);

        Request request = new Request();
        request.setEventId(event.getId());
        request.setRequesterId(requester.getId());
        request.setCreated(LocalDateTime.now());
        request.setStatus(event.getParticipantLimit() == 0 ? Status.CONFIRMED : Status.PENDING);

        if (!event.getRequestModeration()) {
            request.setStatus(Status.CONFIRMED);
        }

        Request savedRequest = requestRepository.save(request);
        log.info("Request created with id: {}", savedRequest.getId());
        return requestMapper.toParticipationRequestDtoOut(savedRequest);
    }

    @Override
    public ParticipationRequestDtoOut cancel(Long userId, Long requestId) {
        log.info("Cancelling request with id: {} for user with id: {}", requestId, userId);

        Request request = requestRepository.findById(requestId).orElseThrow(() -> {
            log.warn("Request with id={} not found", requestId);
            return new NotFoundException(String.format("Request with id=%d was not found", requestId));
        });

        if (!request.getRequesterId().equals(userId)) {
            log.warn("User with id={} cannot cancel non-his request with id={}", userId, requestId);
            throw new ConflictException(String.format(
                    "User with id=%d cannot cancel non-his request with id=%d", userId, requestId));
        }

        request.setStatus(Status.CANCELED);
        Request updatedRequest = requestRepository.save(request);

        log.info("Request with id: {} cancelled successfully", requestId);
        return requestMapper.toParticipationRequestDtoOut(updatedRequest);
    }

    public ParticipationRequestDtoOut getById(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Request with id = " + id + " not found"));
        return requestMapper.toParticipationRequestDtoOut(request);
    }

    @Override
    public List<ParticipationRequestDtoOut> getByIds(List<Long> requestIds) {
        List<Request> foundRequests = requestRepository.findAllByIdIn(requestIds);

        Set<Long> foundIds = foundRequests.stream()
                .map(Request::getId)
                .collect(Collectors.toSet());

        List<Long> notFoundIds = requestIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!notFoundIds.isEmpty()) {
            throw new NotFoundException("Requests with IDs " + notFoundIds + " not found.");
        }

        return foundRequests.stream()
                .map(requestMapper::toParticipationRequestDtoOut)
                .toList();
    }

    @Override
    public List<ParticipationRequestDtoOut> getByEventId(Long eventId) {
        List<Request> foundRequests = requestRepository.findAllByEventId(eventId);
        return foundRequests.stream()
                .map(requestMapper::toParticipationRequestDtoOut)
                .toList();
    }

    @Transactional
    @Override
    public void setStatusRequests(List<Long> requestIds, Status status) {
        requestRepository.setStatusForAllByIdIn(requestIds, status);
    }

    @Override
    public List<Object[]> getCountRequestByEventId(List<Long> eventIds, Status status) {
        return requestRepository.countAllByEventIdInAndStatus(eventIds, status);
    }

    private void validateRequestCreation(UserShortDto requester, EventFullDto event) {
        if (requestRepository.existsByRequesterIdAndEventId(requester.getId(), event.getId())) {
            log.warn("Request already exists for user with id={} and event with id={}", requester.getId(), event.getId());
            throw new ConflictException(String.format(
                    "Request already exists for user with id=%d and event with id=%d", requester.getId(), event.getId()));
        }

        if (event.getInitiator().getId().equals(requester.getId())) {
            log.warn("Event initiator with id={} cannot create a request for his event with id={}", requester.getId(), event.getId());
            throw new ConflictException(String.format(
                    "Event initiator with id=%d cannot create a request for his event with id=%d", requester.getId(), event.getId()));
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            log.warn("Cannot participate in unpublished event with id={}", event.getId());
            throw new ConflictException(String.format("Cannot participate in unpublished event with id=%d", event.getId()));
        }

        if (event.getParticipantLimit() > 0 &&
                requestRepository.countByEventIdAndStatus(event.getId(), Status.CONFIRMED) >= event.getParticipantLimit()) {
            log.warn("Participant limit reached for event with id={}", event.getId());
            throw new ConflictException(String.format("Participant limit reached for event with id=%d", event.getId()));
        }

    }

    private UserShortDto findUserById(Long userId) {
        return userClient.getById(userId);
    }
}
