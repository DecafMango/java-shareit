package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderNames;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @GetMapping
    public List<RequestDto> getUserRequests(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long userId) {
        log.info("Getting all requests from user with id={}", userId);
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAllRequests(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long userId,
                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting all requests for user with id={}, from={}, size={}", userId, from, size);
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequest(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long userId,
                                             @Positive @PathVariable long requestId) {
        log.info("Getting request with id={} from user with id={}", requestId, userId);
        return requestService.getRequest(userId, requestId);
    }

    @PostMapping
    public RequestDto createItemRequest(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long requestorId,
                                                    @Valid @RequestBody RequestDto requestDto) {
        log.info("Creating request {} from user with id={}", requestDto, requestorId);
        return requestService.createRequest(requestorId, requestDto);
    }
}