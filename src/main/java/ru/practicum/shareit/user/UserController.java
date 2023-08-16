package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Начало обработки запроса на получение всех пользователей");
        List<UserDto> usersDto = userService.getUsers();
        log.info("Завершение обработки запроса на получение всех пользователей");
        return usersDto;
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable @Positive Long userId) {
        log.info("Начало обработки запроса на получение пользователя с id={}", userId);
        UserDto userDto = userService.getUser(userId);
        log.info("Завершение обработки запроса на получение пользователя с id={}", userId);
        return userDto;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody User user) {
        log.info("Начало обработки запроса на создание пользователя: {}", user);
        UserDto userDto = userService.createUser(user);
        log.info("Завершение обработки запроса на создание пользователя: {}", userDto);
        return userDto;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody User updatedUser, @PathVariable @Positive Long userId) {
        log.info("Начало обработки запроса на обновление пользователя с id={}: {}", userId, updatedUser);
        UserDto userDto = userService.updateUser(updatedUser, userId);
        log.info("Завершение обарботки запроса на обновление пользователя: {}", userDto);
        return userDto;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable @Positive Long userId) {
        log.info("Начало обработки запроса на удаление пользователя с id={}", userId);
        userService.deleteUser(userId);
        log.info("Завершение обработки запроса на удаление пользователя с id={}", userId);
    }
}
