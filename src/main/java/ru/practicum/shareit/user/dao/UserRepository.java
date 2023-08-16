package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserRepository {
    List<UserDto> getUsers();

    UserDto getUser(Long userId);

    UserDto createUser(User user);

    UserDto updateUser(User updatedUser, Long userId);

    void deleteUser(Long userId);
}
