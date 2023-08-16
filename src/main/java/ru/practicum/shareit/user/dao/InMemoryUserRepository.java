package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailAlreadyUsedException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public List<UserDto> getUsers() {
        return users.keySet().stream().map(id -> UserMapper.toUserDto(users.get(id), id)) // User -> UserDto
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = users.get(userId);
        if (user == null) throw new ObjectNotFoundException("Пользователь с id=" + userId + " не найден");
        return UserMapper.toUserDto(user, userId);
    }

    @Override
    public UserDto createUser(User user) {
        checkEmail(user.getEmail(), null);
        idCounter++;
        users.put(idCounter, user);
        return UserMapper.toUserDto(user, idCounter);
    }

    @Override
    public UserDto updateUser(User updatedUser, Long userId) {
        User user = UserMapper.toUser(getUser(userId));
        if (updatedUser.getName() != null) user.setName(updatedUser.getName());
        if (updatedUser.getEmail() != null) {
            checkEmail(updatedUser.getEmail(), userId);
            user.setEmail(updatedUser.getEmail());
        }
        users.put(userId, user);
        return UserMapper.toUserDto(user, userId);
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    private void checkEmail(String email, Long userId) { // Второй параметр может быть null в случае создания пользователя
         /*
        Во-первых, мне не нравится этот "брудфорс" по пользователям (алгоритмическая сложность o(n))
        Во-вторых, стоит ли в репозитории проверять существование почты или перенести проверку в сервис?
         */
        if (userId == null) { // В случае создания пользователя
            for (User createdUser : users.values()) {
                if (createdUser.getEmail().equals(email))
                    throw new EmailAlreadyUsedException("На данный момент существует пользователь, зарегистрированный " + "на почту: " + email);
            }
        } else { // В случае обновления пользователя (учитывается случай, что обновляется почта одного и того же пользователя)
            for (Long createdUserId : users.keySet()) {
                if (!createdUserId.equals(userId) && users.get(createdUserId).getEmail().equals(email)) {
                    throw new EmailAlreadyUsedException("На данный момент существует пользователь, зарегистрированный " + "на почту: " + email);
                }
            }
        }
    }
}
