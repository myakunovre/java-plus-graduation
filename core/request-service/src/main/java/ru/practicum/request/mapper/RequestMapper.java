package ru.practicum.request.mapper;

import interaction.model.request.ParticipationRequestDtoOut;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.request.model.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(source = "eventId", target = "event")
    @Mapping(source = "requesterId", target = "requester")
    ParticipationRequestDtoOut toParticipationRequestDtoOut(Request request);
}
