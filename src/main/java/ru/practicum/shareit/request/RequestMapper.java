package ru.practicum.shareit.request;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {

    // RequestDto --> Request
    public static Request toRequest(RequestDto requestDto, User requestor) {
        return new Request(null, requestDto.getDescription(), requestor, Collections.emptyList(), requestDto.getCreated());
    }

    // Request --> RequestDto
    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(request.getId(), request.getDescription(), request.getCreated(),
                request.getItems().stream()
                        .map(ItemMapper::toItemDtoForRequest)
                        .collect(Collectors.toList()));
    }

}
