package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
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

    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUser(Long userId) {
        User user = checkUser(userId);
        return UserMapper.toUserDto(user);
    }

    public UserDto createUser(UserDto userDto) {
        //checkEmail(userDto.getEmail(), null);
        UserValidator.checkAllFields(userDto);
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    public UserDto updateUser(UserDto updatedUserDto) {
        UserValidator.checkNotNullFields(updatedUserDto);
        User user = checkUser(updatedUserDto.getId());
        if (updatedUserDto.getName() != null)
            user.setName(updatedUserDto.getName());
        if (updatedUserDto.getEmail() != null) {
            checkEmail(updatedUserDto.getEmail(), updatedUserDto.getId());
            user.setEmail(updatedUserDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    public void deleteUser(Long userId) {
        checkUser(userId);
        userRepository.deleteById(userId);
    }

    private void checkEmail(String email, Long userId) { // Второй параметр может быть null в случае создания пользователя
        if (userId == null) { // В случае создания пользователя
            for (User registeredUser : userRepository.findAll()) {
                if (registeredUser.getEmail().equals(email))
                    throw new EmailAlreadyUsedException("На данный момент существует пользователь, зарегистрированный " + "на почту: " + email);
            }
        } else { // В случае обновления пользователя (учитывается случай, что обновляется почта одного и того же пользователя)
            for (User registeredUser : userRepository.findAll()) {
                if (!registeredUser.getId().equals(userId)
                        && registeredUser.getEmail().equals(email)) {
                    throw new EmailAlreadyUsedException("На данный момент существует пользователь, зарегистрированный " + "на почту: " + email);
                }
            }
        }
    }

    private User checkUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty())
            throw new ObjectNotFoundException("Пользователя с id=" + userId + " не существует");
        return userOptional.get();
    }
}
