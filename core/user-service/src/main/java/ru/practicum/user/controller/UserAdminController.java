package ru.practicum.user.controller;

import interaction.model.user.in.NewUserRequest;
import interaction.model.user.in.UserAdminParam;
import interaction.model.user.output.UserDto;
import interaction.model.user.output.UserShortDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/users")
public class UserAdminController {
    private final UserService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAll(@RequestParam(required = false, defaultValue = "") List<Long> ids,
                                @PositiveOrZero @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                @Positive @RequestParam(required = false, defaultValue = "10") @Min(0) int size) {
        return service.getAll(new UserAdminParam(ids, from, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@Valid @RequestBody NewUserRequest newUserRequest) {
        return service.add(newUserRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    public UserShortDto getById(@PathVariable("id") Long id) {
        log.info("Запрос пользователя id={} от микросервиса", id);
        return service.getById(id);
    }

    @GetMapping("/by-ids")
    public List<UserShortDto> getByIds(@RequestParam List<Long> userIds) {
        log.info("Запрос микросервисом пользователей с ID {}", userIds);
        return service.getByIds(userIds);
    }
}