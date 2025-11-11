package ru.practicum.user.mapper;

import interaction.model.user.in.NewUserRequest;
import interaction.model.user.output.UserDto;
import interaction.model.user.output.UserShortDto;
import org.mapstruct.Mapper;
import ru.practicum.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(NewUserRequest newUserRequest);

    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);
}
