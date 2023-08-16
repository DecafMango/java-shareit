package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> getUsers() {
        return userRepository.getUsers();
    }

    public UserDto getUser(Long userId) {
        return userRepository.getUser(userId);
    }

    public UserDto createUser(User user) {
        UserValidator.checkAllFields(user);
        return userRepository.createUser(user);
    }

    public UserDto updateUser(User updatedUser, Long userId) {
        UserValidator.checkNotNullFields(updatedUser);
        return userRepository.updateUser(updatedUser, userId);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }
}
