package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("User validator test")
public class UserMapperTest {

    @Test
    @DisplayName("should map from UserDto to User")
    public void should_map_from_UserDto_to_User() {
        UserDto userDto = new UserDto(null, "name", "email@email");
        User user = UserMapper.toUser(userDto);

        assertThat(user.getId(), is(userDto.getId()));
        assertThat(user.getName(), is(userDto.getName()));
        assertThat(user.getEmail(), is(userDto.getEmail()));
    }

    @Test
    @DisplayName("should map from User to UserDto")
    public void should_map_from_User_to_UserDto() {
        User user = new User(1L, "name", "email@email");
        UserDto userDto = UserMapper.toUserDto(user);

        assertThat(userDto.getId(), is(user.getId()));
        assertThat(userDto.getName(), is(user.getName()));
        assertThat(userDto.getEmail(), is(user.getEmail()));
    }
}
