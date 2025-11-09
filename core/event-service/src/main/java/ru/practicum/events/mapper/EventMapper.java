package ru.practicum.events.mapper;

import interaction.model.user.output.UserShortDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.category.model.Category;
import interaction.model.event.in.NewEventDto;
import interaction.model.event.in.UpdateEventAdminRequest;
import interaction.model.event.in.UpdateEventUserRequest;
import interaction.model.event.output.EventFullDto;
import interaction.model.event.output.EventShortDto;
import ru.practicum.events.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "initiator", source = "userShortDto")
    @Mapping(target = "id", source = "event.id")
    EventShortDto toEventShortDto(Event event, UserShortDto userShortDto);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "initiatorId", source = "userId")
    @Mapping(target = "id", ignore = true)
    Event toEvent(NewEventDto newEventDto, Category category, Long userId);

    @Mapping(target = "initiator", source = "userShortDto")
    @Mapping(target = "id", source = "event.id")
    EventFullDto toEventFullDto(Event event, UserShortDto userShortDto);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiatorId", source = "userId")
    @Mapping(target = "id", ignore = true)
    Event toEvent(UpdateEventUserRequest updateEventUserRequest, Category category, Long userId);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiatorId", source = "userId")
    @Mapping(target = "id", ignore = true)
    Event toEvent(UpdateEventAdminRequest updateEventAdminRequest, Category category, Long userId);
}
