package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EmailAlreadyUsedException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("User controller test")
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private final UserDto userDto = new UserDto(1L, "name", "gmail@email");

    @Test
    @DisplayName("should return all users")
    public void should_return_all_users() throws Exception {
        when(userService.getUsers())
                .thenReturn(List.of(userDto, userDto));

        List<UserDto> expectedResult = List.of(userDto, userDto);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResult)));

        verify(userService, times(1))
                .getUsers();
    }

    @Test
    @DisplayName("should return user by id=1")
    public void should_return_user_by_id_1() throws Exception {
        when(userService.getUser(anyLong()))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1))
                .getUser(anyLong());
    }

    @Test
    @DisplayName("should not return user that does not exist")
    public void should_not_return_user_that_does_not_exist() throws Exception {
        when(userService.getUser(anyLong()))
                .thenThrow(ObjectNotFoundException.class);

        mockMvc.perform(get("/users/9999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1))
                .getUser(anyLong());
    }

    @Test
    @DisplayName("should create user")
    public void should_create_user() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1))
                .createUser(any());
    }

    @Test
    @DisplayName("should not create user due to invalid email")
    public void should_not_create_user_due_to_invalid_email() throws Exception {
        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(new UserDto(null, null, "wrong_email")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());

        verify(userService, never())
                .createUser(any());
    }

    @Test
    @DisplayName("should not create user due to used email")
    public void should_not_create_user_due_to_used_email() throws Exception {
        when(userService.createUser(any()))
                .thenThrow(EmailAlreadyUsedException.class);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isConflict());

        verify(userService, times(1))
                .createUser(any());
    }

    @Test
    @DisplayName("should update user")
    public void should_update_user() throws Exception {
        when(userService.updateUser(any()))
                .thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(new UserDto(null, "name", null)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1))
                .updateUser(any());
    }

    @Test
    @DisplayName("should not update user that does not exit")
    public void should_not_update_user_that_does_not_exist() throws Exception {
        when(userService.updateUser(any()))
                .thenThrow(ObjectNotFoundException.class);

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(new UserDto(null, "name", null)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1))
                .updateUser(any());
    }

    @Test
    @DisplayName("should delete user")
    public void should_delete_user() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .content(mapper.writeValueAsString(new UserDto(null, "name", null)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .deleteUser(anyLong());
    }

    @Test
    @DisplayName("should not delete user that does not exist")
    public void should_not_delete_user_that_does_not_exist() throws Exception {
        doThrow(ObjectNotFoundException.class)
                .when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/1")
                        .content(mapper.writeValueAsString(new UserDto(null, "name", null)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1))
                .deleteUser(anyLong());
    }
}
