package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.validator.UserValidator;

import java.util.Map;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getUsers() {
        return get("");
    }

    public ResponseEntity<Object> getUser(Long userId) {
        Map<String, Object> parameters = Map.of(
                "userId", userId
        );
        return get("/{userId}", parameters);
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        UserValidator.checkAllFields(userDto);
        return post("", userDto);
    }

    public ResponseEntity<Object> updateUser(long userId, UserDto userDto) {
        UserValidator.checkNotNullFields(userDto);
        Map<String, Object> parameters = Map.of(
                "userId", userId
        );
        return patch("/{userId}", parameters, userDto);
    }

    public ResponseEntity<Object> deleteUser(long userId) {
        Map<String, Object> parameters = Map.of(
                "userId", userId
        );
        return delete("/{userId}", parameters);
    }
}
