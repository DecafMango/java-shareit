package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.HeaderNames;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RequestService requestService;

    @Autowired
    private MockMvc mvc;

    private RequestDto receivedValidRequestDto = new RequestDto(
            null,
            "description",
            null,
            null
    );

    private RequestDto receivedInvalidRequestDto = new RequestDto(
            null,
            "",
            null,
            null
    );

    private RequestDto fullRequestDto = new RequestDto(
            1L,
            "Description",
            LocalDateTime.of(2023, 10, 10, 10, 10, 10),
            Collections.emptyList()
    );

    @Test
    public void receiveValidRequestDto() throws Exception {
        when(requestService.createItemRequest(any(), anyLong()))
                .thenReturn(fullRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(receivedValidRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HeaderNames.USER_ID_HEADER, 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(fullRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(fullRequestDto.getDescription())))
                .andExpect(jsonPath("$.items", is(fullRequestDto.getItems())));
    }

    @Test
    public void receiveInvalidRequestDto() throws Exception {
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(receivedInvalidRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HeaderNames.USER_ID_HEADER, 10))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void receiveUserRequests() throws Exception {
        when(requestService.getUserRequests(anyLong()))
                .thenReturn(List.of(fullRequestDto, fullRequestDto));

        mvc.perform(get("/requests")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HeaderNames.USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(fullRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(fullRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].items", is(fullRequestDto.getItems())))
                .andExpect(jsonPath("$[1].id", is(fullRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(fullRequestDto.getDescription())))
                .andExpect(jsonPath("$[1].items", is(fullRequestDto.getItems())));
    }

    @Test
    public void receiveRequestDtoById() throws Exception {
        when(requestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(fullRequestDto);

        mvc.perform(get("/requests/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(HeaderNames.USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(fullRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(fullRequestDto.getDescription())))
                .andExpect(jsonPath("$.items", is(fullRequestDto.getItems())));

    }

    @Test
    public void receiveAllRequests() throws Exception {
        when(requestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(fullRequestDto, fullRequestDto));

        mvc.perform(get("/requests/all?from=0&size=2")
                .accept(MediaType.APPLICATION_JSON)
                .header(HeaderNames.USER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(fullRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(fullRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].items", is(fullRequestDto.getItems())))
                .andExpect(jsonPath("$[1].id", is(fullRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(fullRequestDto.getDescription())))
                .andExpect(jsonPath("$[1].items", is(fullRequestDto.getItems())));
    }

    @Test
    public void receiveFromUnexcitingUser() throws Exception {
        when(requestService.getRequestById(anyLong(), anyLong()))
                .thenThrow(new ObjectNotFoundException("Пользователя не существует"));

        mvc.perform(get("/requests/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HeaderNames.USER_ID_HEADER, 99999))
                .andExpect(status().isNotFound());
    }
}
