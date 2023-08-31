package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailAlreadyUsedException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long userId) {
        User user = checkUser(userId);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        UserValidator.checkAllFields(userDto);
        try {
            return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
        } catch (Exception e) {
            throw new EmailAlreadyUsedException("На данный момент существует пользователь, зарегистрированный " +
                    "на почту: " + userDto.getEmail());
        }
    }

    @Transactional
    public UserDto updateUser(UserDto updatedUserDto) {
        UserValidator.checkNotNullFields(updatedUserDto);
        User user = checkUser(updatedUserDto.getId());
        if (updatedUserDto.getName() != null)
            user.setName(updatedUserDto.getName());
        if (updatedUserDto.getEmail() != null) {
            user.setEmail(updatedUserDto.getEmail());
        }
        try {
            return UserMapper.toUserDto(userRepository.save(user));
        } catch (Exception e) {
            throw new EmailAlreadyUsedException("На данный момент существует пользователь, зарегистрированный " +
                    "на почту: " + user.getEmail());
        }
    }

    @Transactional
    public void deleteUser(Long userId) {
        checkUser(userId);
        userRepository.deleteById(userId);
    }

    private User checkUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty())
            throw new ObjectNotFoundException("Пользователя с id=" + userId + " не существует");
        return userOptional.get();
    }
}
