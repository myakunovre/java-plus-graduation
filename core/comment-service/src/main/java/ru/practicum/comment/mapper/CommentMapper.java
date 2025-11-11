package ru.practicum.comment.mapper;

import interaction.model.comment.in.NewCommentDto;
import interaction.model.comment.output.CommentFullDto;
import interaction.model.comment.output.CommentShortDto;
import interaction.model.event.output.EventShortDto;
import interaction.model.user.output.UserShortDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.comment.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "authorId", source = "userId")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "modifiedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "id", ignore = true)
    Comment toComment(NewCommentDto commentDto, EventShortDto event, Long userId);

    @Mapping(target = "author", source = "userShortDto")
    @Mapping(target = "id", source = "comment.id")
    CommentFullDto toCommentDto(Comment comment, UserShortDto userShortDto, EventShortDto event);

    @Mapping(target = "author", source = "userShortDto")
    @Mapping(target = "id", source = "comment.id")
    CommentShortDto toCommentShortDto(Comment comment, UserShortDto userShortDto, EventShortDto event);
}