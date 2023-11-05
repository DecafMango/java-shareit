package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Get users");
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@Positive @PathVariable long userId) {
        log.info("Get user with id={}", userId);
        return userService.getUser(userId);
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Create user {}", userDto);
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@Positive @PathVariable long userId,
                              @RequestBody UserDto userDto) {
        userDto.setId(userId);
        log.info("Updating user with id={} following parameters {}", userId, userDto);
        return userService.updateUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@Positive @PathVariable long userId) {
        log.info("Deleting user with id={}", userId);
        userService.deleteUser(userId);
    }
}