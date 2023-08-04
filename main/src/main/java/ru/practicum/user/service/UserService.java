package ru.practicum.user.service;

import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<UserDto> getAllUsers(Set<Long> ids, Integer from, Integer size);

    UserDto addUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);

    User findUserById(Long id);
}
