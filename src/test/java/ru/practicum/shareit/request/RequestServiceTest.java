package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Transactional
@SpringBootTest(
        properties = "spring.datasource.url=jdbc:h2:file:./db/filmorate_test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTest {

    private final RequestService requestService;
    private final UserRepository userRepository;

    @Test
    public void createRequest() {
        User createdUser = userRepository.findById(1L).get();

        RequestDto requestDto = new RequestDto(null, "description", LocalDateTime.now(), null);
        RequestDto createdRequest = requestService.createItemRequest(requestDto, createdUser.getId());

        assertThat(createdRequest.getDescription(), equalTo(requestDto.getDescription()));
    }

    @Test
    public void createRequestByUnexistingUser() {
        RequestDto requestDto = new RequestDto(null, "description", LocalDateTime.now(), null);
        Assertions.assertThrows(ObjectNotFoundException.class, () -> requestService.createItemRequest(requestDto, -1L));

    }

    @Test
    public void getRequestById() {
        User createdUser = userRepository.findById(1L).get();

        RequestDto requestDto = new RequestDto(null, "description", LocalDateTime.now(), null);
        RequestDto createdRequest = requestService.createItemRequest(requestDto, createdUser.getId());

        RequestDto foundRequest = requestService.getRequestById(createdRequest.getId(), createdUser.getId());

        assertThat(foundRequest.getId(), equalTo(createdRequest.getId()));
        assertThat(foundRequest.getDescription(), equalTo(createdRequest.getDescription()));
        assertThat(foundRequest.getCreated(), equalTo(createdRequest.getCreated()));
    }

    @Test
    public void getRequestByIdByUnexistingUser() {
        Assertions.assertThrows(ObjectNotFoundException.class, () -> requestService.getRequestById(1L, 9999L));
    }

    @Test
    public void getRequestByIdThatDoesNotExist() {
        Assertions.assertThrows(ObjectNotFoundException.class, () -> requestService.getRequestById(9999L, 1L));
    }

    @Test
    public void getUserRequests() {
        User user = userRepository.findById(1L).get();

        RequestDto requestDto1 = new RequestDto(null, "description1", LocalDateTime.now(), null);
        RequestDto createdRequest1 = requestService.createItemRequest(requestDto1, user.getId());

        RequestDto requestDto2 = new RequestDto(null, "description", LocalDateTime.now(), null);
        RequestDto createdRequest2 = requestService.createItemRequest(requestDto2, user.getId());

        List<RequestDto> userRequests = requestService.getUserRequests(user.getId());

        assertThat(userRequests.get(0).getDescription(), equalTo(createdRequest2.getDescription()));
        assertThat(userRequests.get(0).getCreated(), equalTo(createdRequest2.getCreated()));
        assertThat(userRequests.get(1).getDescription(), equalTo(createdRequest1.getDescription()));
        assertThat(userRequests.get(1).getCreated(), equalTo(createdRequest1.getCreated()));
    }

    @Test
    public void getUserRequestsByUserTharDoesNotExist() {
        Assertions.assertThrows(ObjectNotFoundException.class, () -> requestService.getUserRequests(9999L));
    }

    @Test
    public void getAllRequests() {
        User user1 = userRepository.findById(1L).get();
        User user2 = userRepository.findById(2L).get();

        RequestDto requestDto = new RequestDto(null, "description1", LocalDateTime.now(), null);
        RequestDto createdRequest = requestService.createItemRequest(requestDto, user1.getId());

        List<RequestDto> allRequestsForUser1 = requestService.getAllRequests(user1.getId(), 0, 1);
        assertThat(allRequestsForUser1.size(), is(0));

        List<RequestDto> allRequestsForUser2 = requestService.getAllRequests(user2.getId(), 0, 1);
        assertThat(allRequestsForUser2.get(0).getDescription(), equalTo(createdRequest.getDescription()));
    }

    @Test
    public void getAllRequestByUserThatDoesNotExist() {
        Assertions.assertThrows(ObjectNotFoundException.class, () -> requestService.getAllRequests(9999L, 0, 1));
    }
}
