package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RequestMapperTest {

    private final User user = new User(1L, "name", "email@yandex.ru");
    private final Request request = new Request(1L, "description", user, Collections.emptyList(), LocalDateTime.now());
    private final RequestDto requestDto = new RequestDto(1L, "description", LocalDateTime.now(), Collections.emptyList());

    @Test
    public void mapToRequestDto() {
        RequestDto mappedRequestDto = RequestMapper.toRequestDto(request);

        assertThat(mappedRequestDto.getId(), equalTo(request.getId()));
        assertThat(mappedRequestDto.getDescription(), equalTo(request.getDescription()));
        assertThat(mappedRequestDto.getCreated(), equalTo(request.getCreated()));
        assertThat(mappedRequestDto.getItems(), equalTo(request.getItems()));
    }

    @Test
    public void mapToRequest() {
        Request mappedRequest = RequestMapper.toRequest(requestDto, user);

        assertThat(mappedRequest.getId(), equalTo(requestDto.getId()));
        assertThat(mappedRequest.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(mappedRequest.getCreated(), equalTo(requestDto.getCreated()));
        assertThat(mappedRequest.getItems(), equalTo(requestDto.getItems()));
        assertThat(mappedRequest.getRequestor(), equalTo(user));
    }

}
