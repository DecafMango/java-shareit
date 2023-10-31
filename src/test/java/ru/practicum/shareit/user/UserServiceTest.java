package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailAlreadyUsedException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("User service test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    private final EntityManager em;
    private final UserService userService;
    private final UserRepository userRepository;


    @Test
    @Order(value = 1)
    @DisplayName("should return all users")
    public void should_return_all_users() {
        List<UserDto> usersDto = userService.getUsers();

        assertThat(usersDto.get(0).getId(), is(1L));
        assertThat(usersDto.get(0).getName(), is("name1"));
        assertThat(usersDto.get(0).getEmail(), is("email1@email"));
        assertThat(usersDto.get(1).getId(), is(2L));
        assertThat(usersDto.get(1).getName(), is("name2"));
        assertThat(usersDto.get(1).getEmail(), is("email2@email"));

        System.out.println(userService.getUsers());
    }

    @Test
    @Order(value = 2)
    @DisplayName("should return user id=1")
    public void should_return_user_id_1() {
        UserDto userDto = userService.getUser(1L);

        assertThat(userDto.getId(), is(1L));
        assertThat(userDto.getName(), is("name1"));
        assertThat(userDto.getEmail(), is("email1@email"));
    }

    @Test
    @Order(value = 3)
    @DisplayName("should not return user that does not exist")
    public void should_not_return_user_that_does_not_exist() {
        assertThrows(ObjectNotFoundException.class, () -> userService.getUser(-1L));
    }

    @Test
    @Order(value = 4)
    @DisplayName("should not create user due to used email")
    public void should_not_create_user_due_to_used_email() {
        UserDto userDto = new UserDto(null, "name1", "email1@email");

        assertThrows(EmailAlreadyUsedException.class, () -> userService.createUser(userDto));
    }

    @Test
    @Order(value = 5)
    @DisplayName("should update user")
    public void should_update_user() {
        UserDto userDto = new UserDto(1L, "updated", "updated@email");

        UserDto updatedUserDto = userService.updateUser(userDto);

        TypedQuery<User> query = em.createQuery("select u from User as u where id=:id", User.class);
        User user = query.setParameter("id", updatedUserDto.getId()).getSingleResult();

        assertThat(user.getId(), is(userDto.getId()));
        assertThat(user.getName(), is(userDto.getName()));
        assertThat(user.getEmail(), is(userDto.getEmail()));
    }

    @Test
    @Order(value = 6)
    @DisplayName("should not update user that does not exist")
    public void should_not_update_user_that_does_not_exist() {
        UserDto userDto = new UserDto(-1L, "name3", "email3@email");

        assertThrows(ObjectNotFoundException.class, () -> userService.updateUser(userDto));
    }

    @Test
    @Order(value = 7)
    @DisplayName("should delete user")
    public void should_delete_user() {
        userService.deleteUser(1L);
    }

    @Test
    @Order(value = 8)
    @DisplayName("should not delete user that does not exist")
    public void should_not_delete_user_that_does_not_exist() {
        assertThrows(ObjectNotFoundException.class, () -> userService.deleteUser(-1L));
    }
}
