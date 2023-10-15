package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestService {
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Transactional(readOnly = true)
    public List<RequestDto> getUserRequests(Long userId) {
        checkUser(userId);
        List<Request> requests = requestRepository.getAllByRequestor_IdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        checkUser(userId);

        if (from == null && size == null)
            return Collections.emptyList();

        if (from == null || size == null || from < 0 || size <= 0)
            throw new ValidationException("Неверно введены from и size");

        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        Pageable page = PageRequest.of(from / size, size, sort);

        Page<Request> requestPage = requestRepository.getAllByRequestor_IdNotOrderByCreatedDesc(userId, page);
        return requestPage.getContent().stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RequestDto getRequestById(Long requestId, Long userId) {
        checkUser(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Запроса с id=" + requestId + " не существует"));
        return RequestMapper.toRequestDto(request);
    }

    public RequestDto createItemRequest(RequestDto requestDto, Long requestorId) {
        User requestor = checkUser(requestorId);
        return RequestMapper.toRequestDto(requestRepository.save(RequestMapper.toRequest(requestDto, requestor)));
    }


    private User checkUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty())
            throw new ObjectNotFoundException("Пользователя с id=" + userId + " не существует");
        return userOptional.get();
    }
}
