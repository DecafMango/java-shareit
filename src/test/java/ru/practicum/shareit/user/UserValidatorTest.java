package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("User validator test")
public class UserValidatorTest {

    @Test
    @DisplayName("should validate user")
    public void should_validate_user() {
        UserDto userDto = new UserDto(1L, "name", "email@email");

        UserValidator.checkAllFields(userDto);
        UserValidator.checkNotNullFields(userDto);
    }

    @Test
    @DisplayName("should not validate user due to invalid name")
    public void should_not_validate_user_due_invalid_name() {
        UserDto userDto = new UserDto(1L, "", "email@email");

        assertThrows(ValidationException.class, () -> UserValidator.checkAllFields(userDto));
        assertThrows(ValidationException.class, () -> UserValidator.checkNotNullFields(userDto));
    }

    @Test
    @DisplayName("should not validate user due to invalid email")
    public void should_not_validate_user_due_to_invalid_email() {
        UserDto userDto = new UserDto(1L, "name", null);

        assertThrows(ValidationException.class, () -> UserValidator.checkAllFields(userDto));
    }
}
