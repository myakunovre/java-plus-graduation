
package ru.practicum.comment.service;

import feign.FeignException;
import interaction.client.EventFeignClient;
import interaction.client.UserFeignClient;
import interaction.exceptions.ConflictException;
import interaction.exceptions.ForbiddenException;
import interaction.exceptions.NotFoundException;
import interaction.model.comment.in.*;
import interaction.model.event.State;
import interaction.model.event.output.EventFullDto;
import interaction.model.event.output.EventShortDto;
import interaction.model.user.output.UserShortDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import interaction.model.comment.output.CommentFullDto;
import interaction.model.comment.output.CommentShortDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserFeignClient userClient;
    private final EventFeignClient eventClient;

    public CommentShortDto  create(NewCommentDto newCommentDto, Long userId, Long eventId) {
        UserShortDto user = checkUserIfExists(userId);
        EventFullDto eventFullDto = getEventFullDto(eventId);
        EventShortDto eventShortDto = getEventShortDto(eventId);

        if (eventFullDto.getState() != State.PUBLISHED) {
            throw new ConflictException("Cannot add comment because the event it's not status published : "
                    + eventFullDto.getState().name());
        }

        Comment comment = commentMapper.toComment(newCommentDto, eventShortDto, userId);
        if (comment.getState() == null) {
            comment.setState(State.PENDING);
        }
        comment = commentRepository.save(comment);

        return commentMapper.toCommentShortDto(comment, user, eventShortDto);
    }

    public void delete(CommentParam param) {
        checkUserIfExists(param.getUserId());
        getEventShortDto(param.getEventId());
        Comment comment = checkCommentIfExists(param.getCommentId());

        if (!comment.getAuthorId().equals(param.getUserId())) {
            throw new ForbiddenException("User with id " + param.getUserId() + " is not author of comment " + comment.getId());
        }

        commentRepository.deleteById(param.getCommentId());
        log.info("Comment {} was deleted", comment);
    }

    public void delete(Long commentId) {
        checkCommentIfExists(commentId);
        commentRepository.deleteById(commentId);
        log.info("Comment with id = {} was deleted by admin", commentId);
    }

    public CommentFullDto update(NewCommentDto newComment, CommentParam param) {
        UserShortDto user = checkUserIfExists(param.getUserId());
        EventShortDto eventShortDto = getEventShortDto(param.getEventId());
        Comment existingComment = checkCommentIfExists(param.getCommentId());

        if (!existingComment.getAuthorId().equals(param.getUserId())) {
            throw new ForbiddenException("User with id " + param.getUserId() + " is not author of comment " + existingComment.getId());
        }

        if (existingComment.getState() == State.PUBLISHED) {
            existingComment.setState(State.PENDING);
        } else if (existingComment.getState() == State.CANCELED) {
            throw new ConflictException("Cannot update comment with id: " + existingComment.getId()
                    + " because status: " + existingComment.getState());
        }

        existingComment.setText(newComment.getText());

        Comment updatedComment = commentRepository.save(existingComment);
        log.info("Comment was updated with id={}, old name='{}', new name='{}'",
                param.getCommentId(), existingComment.getText(), newComment.getText());
        return commentMapper.toCommentDto(updatedComment, user, eventShortDto);
    }

    public CommentFullDto update(Long commentId, String filter) {

        State stateForUpdating = toState(filter);
        Comment existingComment = checkCommentIfExists(commentId);

        if (existingComment.getState() != State.PENDING) {
            throw new ConflictException("Cannot update comment with state not PENDING");
        }

        existingComment.setState(stateForUpdating);

        if (stateForUpdating.equals(State.PUBLISHED)) {
            if (existingComment.getPublishedOn() != null) {
                existingComment.setModifiedOn(LocalDateTime.now());
            } else {
                existingComment.setPublishedOn(LocalDateTime.now());
            }
        }

        Comment updatedComment = commentRepository.save(existingComment);
        log.info("Comment with id={} was updated with status {}", commentId, stateForUpdating);

        UserShortDto user = checkUserIfExists(updatedComment.getAuthorId());
        EventShortDto event = getEventShortDto(updatedComment.getEventId());
        return commentMapper.toCommentDto(updatedComment, user, event);
    }

    public CommentFullDto getComment(CommentParam param) {
        checkUserIfExists(param.getUserId());
        getEventShortDto(param.getEventId());
        Comment comment = checkCommentIfExists(param.getCommentId());

        if (!comment.getAuthorId().equals(param.getUserId()) && comment.getState() != State.PUBLISHED) {
            throw new ForbiddenException("Cannot get comment with id: " + param.getEventId()
                    + " because it's not status published: " + comment.getState());
        }

        if (!comment.getAuthorId().equals(param.getUserId())) {
            comment.setPublishedOn(null);
            comment.setState(null);
        }

        UserShortDto user = checkUserIfExists(comment.getAuthorId());
        EventShortDto event = getEventShortDto(comment.getEventId());
        return commentMapper.toCommentDto(comment, user, event);
    }

    @Transactional(readOnly = true)
    public List<CommentFullDto> getCommentsByEventId(Long eventId, GetCommentParam param) {
        Long userId = param.getUserId();
        Integer from = param.getFrom();
        Integer size = param.getSize();
        List<Comment> comments;

        checkUserIfExists(param.getUserId());
        getEventShortDto(eventId);

        if (size == 0) {
            comments = commentRepository.findByEventIdAndAuthorIdAndState(userId, eventId, State.PUBLISHED).stream()
                    .skip(from)
                    .toList();
        } else if (from < size && size > 0) {
            PageRequest pageRequest = PageRequest.of(from / size, size);
            comments = commentRepository.findByEventIdAndAuthorIdAndState(eventId, userId, State.PUBLISHED, pageRequest);
        } else {
            return List.of();
        }
        Map<Long, UserShortDto> userMap = getUserMap(comments);
        Map<Long, EventShortDto> eventMap = getEventMap(comments);
        return comments.stream()
                .map(comment -> commentMapper.toCommentDto(
                        comment, userMap.get(comment.getAuthorId()), eventMap.get(comment.getEventId())))
                .toList();
    }


    @Transactional(readOnly = true)
    public List<CommentFullDto> getCommentsByEventId(CommentPublicParam param) {
        getEventShortDto(param.getEventId());

        Integer from = param.getFrom();
        Integer size = param.getSize();
        List<Comment> comments;

        if (size == 0) {
            comments = commentRepository.findByEventIdAndState(param.getEventId(), State.PUBLISHED).stream()
                    .skip(from)
                    .toList();
        } else if (from < size && size > 0) {
            PageRequest pageRequest = PageRequest.of(from / size, size);
            comments = commentRepository.findByEventIdAndState(param.getEventId(), State.PUBLISHED, pageRequest);
        } else {
            return List.of();
        }
        Map<Long, UserShortDto> userMap = getUserMap(comments);
        Map<Long, EventShortDto> eventMap = getEventMap(comments);
        return comments.stream()
                .map(comment -> commentMapper.toCommentDto(
                        comment, userMap.get(comment.getAuthorId()), eventMap.get(comment.getEventId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CommentFullDto> getComments(GetCommentParam param) {
        checkUserIfExists(param.getUserId());

        List<Comment> comments;
        if (param.getSize() == 0) {
            comments = getCommentsWithoutPagination(param);
        } else if (param.getFrom() < param.getSize() && param.getSize() > 0) {
            comments = getCommentsWithPagination(param);
        } else {
            return List.of();
        }

        Map<Long, UserShortDto> userMap = getUserMap(comments);
        Map<Long, EventShortDto> eventMap = getEventMap(comments);
        return comments.stream()
                .map(comment -> commentMapper.toCommentDto(
                        comment, userMap.get(comment.getAuthorId()), eventMap.get(comment.getEventId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CommentFullDto> getComments(CommentAdminParam param) {

        dateValidation(param);

        List<Comment> comments;
        if (param.getSize() == 0) {
            comments = getCommentsWithoutPagination(param);
        } else if (param.getFrom() < param.getSize() && param.getSize() > 0) {
            comments = getCommentsWithPagination(param);
        } else {
            return List.of();
        }

        Map<Long, UserShortDto> userMap = getUserMap(comments);
        Map<Long, EventShortDto> eventMap = getEventMap(comments);
        return comments.stream()
                .map(comment -> commentMapper.toCommentDto(
                        comment, userMap.get(comment.getAuthorId()), eventMap.get(comment.getEventId())))
                .toList();
    }

    private List<Comment> getCommentsWithoutPagination(GetCommentParam param) {
        List<Comment> result = param.getStatus() == StateFilter.ALL
                ? commentRepository.findByAuthorId(param.getUserId())
                : commentRepository.findByAuthorIdAndState(param.getUserId(), toState(param.getStatus()));

        return result.stream()
                .skip(param.getFrom())
                .toList();
    }

    private List<Comment> getCommentsWithPagination(GetCommentParam param) {
        PageRequest pageRequest = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());
        return param.getStatus() == StateFilter.ALL
                ? commentRepository.findByAuthorId(param.getUserId(), pageRequest)
                : commentRepository.findByAuthorIdAndState(param.getUserId(), toState(param.getStatus()), pageRequest);
    }

    private List<Comment> getCommentsWithoutPagination(CommentAdminParam param) {

        List<Comment> result = param.getStatus() == StateFilter.ALL
                ? commentRepository.findByCreatedOnBetween(param.getStart(), param.getEnd())
                : commentRepository.findByStateAndCreatedOnBetween(
                toState(param.getStatus()), param.getStart(), param.getEnd());

        return result.stream()
                .skip(param.getFrom())
                .toList();
    }

    private List<Comment> getCommentsWithPagination(CommentAdminParam param) {
        PageRequest pageRequest = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());
        return param.getStatus() == StateFilter.ALL
                ? commentRepository.findByCreatedOnBetween(param.getStart(), param.getEnd(), pageRequest)
                : commentRepository.findByStateAndCreatedOnBetween(
                toState(param.getStatus()), param.getStart(), param.getEnd(), pageRequest);
    }

    private State toState(StateFilter filter) {
        return switch (filter) {
            case PENDING -> State.PENDING;
            case PUBLISHED -> State.PUBLISHED;
            case CANCELED -> State.CANCELED;
            case ALL -> throw new IllegalArgumentException("ALL is not a valid State");
        };
    }

    private State toState(String filter) {
        return switch (filter) {
            case "APPROVE" -> State.PUBLISHED;
            case "REJECT" -> State.CANCELED;
            default -> throw new IllegalArgumentException(
                    "Parameter action must be APPROVE or REJECT, but action = " + filter);
        };
    }

    private UserShortDto checkUserIfExists(Long userId) {

        try {
            return userClient.getById(userId);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
    }

    private EventFullDto getEventFullDto(Long eventId) {
        return eventClient.getEventFullDtoById(eventId);
    }

    private EventShortDto getEventShortDto(Long eventId) {
        return eventClient.getEventShortDtoById(eventId);
    }

    private Comment checkCommentIfExists(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found: " + commentId));
    }

    private static void dateValidation(CommentAdminParam param) {
        if (param.getStart().isAfter(param.getEnd())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }

    private Map<Long, UserShortDto> getUserMap(List<Comment> comments) {
        List<Long> authorIds = comments.stream()
                .map(Comment::getAuthorId)
                .toList();
        try {
        return userClient.getByIds(authorIds).stream()
                .collect(Collectors.toMap(UserShortDto::getId, identity()));
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("Users not found");
        }
    }

    private Map<Long, EventShortDto> getEventMap(List<Comment> comments) {
        List<Long> eventIds = comments.stream()
                .map(Comment::getEventId)
                .toList();
        try {
            return eventClient.getByIds(eventIds).stream()
                    .collect(Collectors.toMap(EventShortDto::getId, identity()));
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("Events not found");
        }
    }
}
