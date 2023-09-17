package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderNames;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
public class RequestController {
    private final RequestService requestService;

    @GetMapping
    public List<RequestDto> getUserRequests(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER)
                                                     Long userId) {
        log.info("Начало обработки запроса на получение всех запросов пользователя с id={}", userId);
        List<RequestDto> requestsDto = requestService.getUserRequests(userId);
        log.info("Окончание обработки запроса на получение всех запросов пользователя с id={}", userId);
        return requestsDto;
    }

    @GetMapping("/all")
    public List<RequestDto> getAllRequests(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) Long userId,
                                           @RequestParam(required = false) Integer from,
                                           @RequestParam(required = false) Integer size) {
        log.info("Начало обработки запроса на получение {} запросов, начиная с {}, пользователем с id={}",
                size, from, userId);
        List<RequestDto> requestsDto = requestService.getAllRequests(userId, from, size);
        log.info("Окончание обработки запроса на получение {} запросов, начиная с {}, пользователем с id={}",
                size, from, userId);
        return requestsDto;
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequestById(@Positive @PathVariable Long requestId,
                                     @Positive @RequestHeader(HeaderNames.USER_ID_HEADER) Long userId) {
        log.info("Начало обработки запроса на получение запроса с id={} пользователем с id={}", requestId, userId);
        RequestDto requestDto = requestService.getRequestById(requestId, userId);
        log.info("Окончание обработки запроса на получение запроса с id={} пользователем с id={}", requestId, userId);
        return requestDto;
    }

    @PostMapping
    public RequestDto createItemRequest(@Valid @RequestBody RequestDto requestDto,
                                        @Positive @RequestHeader(HeaderNames.USER_ID_HEADER) Long requestorId) {
        log.info("Начало обработки запроса на создание запроса {} пользователем с id={}", requestDto, requestorId);
        requestDto.setCreated(LocalDateTime.now());
        RequestDto createdRequestDto = requestService.createItemRequest(requestDto, requestorId);
        log.info("Окончание обработки запроса на создание запроса {} пользователем с id={}", requestDto,
                requestorId);
        return createdRequestDto;
    }
}
