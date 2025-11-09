package ru.practicum.user.service;



import interaction.model.user.in.NewUserRequest;
import interaction.model.user.in.UserAdminParam;
import interaction.model.user.output.UserDto;
import interaction.model.user.output.UserShortDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAll(UserAdminParam params);

    UserDto add(NewUserRequest newUserRequest);

    void delete(Long id);

    UserShortDto getById(Long id);

    List<UserShortDto> getByIds(List<Long> ids);
}
