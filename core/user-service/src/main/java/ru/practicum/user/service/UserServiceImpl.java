package ru.practicum.user.service;

import interaction.exceptions.DuplicateException;
import interaction.exceptions.NotFoundException;
import interaction.model.user.in.NewUserRequest;
import interaction.model.user.in.UserAdminParam;
import interaction.model.user.output.UserDto;
import interaction.model.user.output.UserShortDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAll(UserAdminParam params) {
        if (params.getSize() == 0) {
            if (params.getIds() != null && !params.getIds().isEmpty()) {
                return repository.findAllByIdIn(params.getIds()).stream()
                        .skip(params.getFrom())
                        .map(mapper::toUserDto)
                        .toList();
            } else {
                return repository.findAll().stream()
                        .skip(params.getFrom())
                        .map(mapper::toUserDto)
                        .toList();
            }

        } else if (params.getFrom() < params.getSize()) {
            Page<User> usersPage;
            int pageNumber = params.getFrom() / params.getSize();
            Pageable pageable = PageRequest.of(pageNumber, params.getSize());

            if (params.getIds() != null && !params.getIds().isEmpty()) {
                usersPage = repository.findAllByIdIn(params.getIds(), pageable);
            } else {
                usersPage = repository.findAll(pageable);
            }

            return usersPage.stream()
                    .map(mapper::toUserDto)
                    .toList();
        } else {
            return List.of();
        }
    }

    @Transactional
    @Override
    public UserDto add(NewUserRequest newUserRequest) {
        if (repository.existsByEmail(newUserRequest.getEmail())) {
            throw new DuplicateException("Email already exists: " + newUserRequest.getEmail());
        }
        User user = repository.save(mapper.toUser(newUserRequest));
        log.info("User was created: {}", user);
        return mapper.toUserDto(user);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException(String.format("User with id=%d was not found", id));
        }
        repository.deleteById(id);
        log.info("User with id={}, was deleted", id);
    }

    @Override
    public UserShortDto getById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
        return mapper.toUserShortDto(user);
    }

    @Override
    public List<UserShortDto> getByIds(List<Long> ids) {
        List<User> foundUsers = repository.findAllByIdIn(ids);

        Set<Long> foundIds = foundUsers.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        List<Long> notFoundIds = ids.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!notFoundIds.isEmpty()) {
            throw new NotFoundException("Users with IDs " + notFoundIds + " not found.");
        }

        return foundUsers.stream()
                .map(mapper::toUserShortDto)
                .toList();
    }
}