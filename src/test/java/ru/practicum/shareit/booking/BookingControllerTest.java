package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.HeaderNames;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@WebMvcTest(controllers = {BookingController.class})
@DisplayName("Booking controller test")
public class BookingControllerTest {

    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private final RequestBookingDto requestBookingDto = new RequestBookingDto(null, LocalDateTime.now(), LocalDateTime.now());
    private final ResponseBookingDto responseBookingDto = new ResponseBookingDto(1L, LocalDateTime.now(), LocalDateTime.now(),
            new ItemDto(1L, "name1", "description1", false, null, null, null, null),
            new UserDto(1L, "name1", "email1@email"), BookingStatus.WAITING);

    @Test
    @DisplayName("should return booking by id")
    public void should_return_booking_by_id() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(responseBookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header(HeaderNames.USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(responseBookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.name", is(responseBookingDto.getBooker().getName())));
    }

    @Test
    @DisplayName("should throw exception due to non-existent id")
    public void should_throw_exception_due_to_non_existent_id() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenThrow(ObjectNotFoundException.class);

        mockMvc.perform(get("/bookings/-1")
                        .header(HeaderNames.USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should throw exception due to non-existent user")
    public void should_throw_exception_due_to_non_existent_user_1() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenThrow(ObjectNotFoundException.class);

        mockMvc.perform(get("/bookings/-1")
                        .header(HeaderNames.USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return all user bookings")
    public void should_return_all_user_bookings() throws Exception {
        when(bookingService.getBookings(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(responseBookingDto));

        mockMvc.perform(get("/bookings?state=ALL&size=1&from=1")
                        .header(HeaderNames.USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1L), Long.class));
    }

    @Test
    @DisplayName("should throw exception due to non-existent user")
    public void should_throw_exception_due_to_non_existent_user_2() throws Exception {
        when(bookingService.getBookings(anyString(), anyLong(), anyInt(), anyInt()))
                .thenThrow(ObjectNotFoundException.class);

        mockMvc.perform(get("/bookings?state=ALL&size=1&from=1")
                        .header(HeaderNames.USER_ID_HEADER, -1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return all owner bookings")
    public void should_return_all_owner_bookings() throws Exception {
        when(bookingService.getOwnerBookings(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(responseBookingDto));

        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=1")
                        .header(HeaderNames.USER_ID_HEADER, -1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1L), Long.class));
    }

    @Test
    @DisplayName("should create booking")
    public void should_create_booking() throws Exception {
        when(bookingService.createBooking(any(), anyLong()))
                .thenReturn(responseBookingDto);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(requestBookingDto))
                        .header(HeaderNames.USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(responseBookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.name", is(responseBookingDto.getBooker().getName())));
        ;
    }

    @Test
    @DisplayName("should not create booking due to non-existent object (user/item)")
    public void should_not_create_booking_due_to_non_existent_object() throws Exception {
        when(bookingService.createBooking(any(), anyLong()))
                .thenThrow(ObjectNotFoundException.class);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(requestBookingDto))
                        .header(HeaderNames.USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should not create booking due to unavailable item")
    public void should_not_create_booking_due_to_unavailable_item() throws Exception {
        when(bookingService.createBooking(any(), anyLong()))
                .thenThrow(ItemUnavailableException.class);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(requestBookingDto))
                        .header(HeaderNames.USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should answer on booking")
    public void should_answer_on_booking() throws Exception {
        when(bookingService.answerOnBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(responseBookingDto);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header(HeaderNames.USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should not answer on booking due to not access")
    public void should_not_answer_on_booking_due_to_not_access() throws Exception {
        when(bookingService.answerOnBooking(anyLong(), anyBoolean(), anyLong()))
                .thenThrow(NoAccessException.class);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header(HeaderNames.USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should not answer on booking due not not found object (user/booking)")
    public void should_not_answer_on_booking_due_to_not_found_object() throws Exception {
        when(bookingService.answerOnBooking(anyLong(),anyBoolean(), anyLong()))
                .thenThrow(ObjectNotFoundException.class);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header(HeaderNames.USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should throw exception due to already answered booking")
    public void should_throw_exception_due_to_already_answered_booking() throws Exception {
        when(bookingService.answerOnBooking(anyLong(),anyBoolean(), anyLong()))
                .thenThrow(BookingIsAlreadyAnswered.class);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header(HeaderNames.USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should throw exception due to invalid booking")
    public void should_throw_exception_due_to_invalid_booking() throws Exception {
        when(bookingService.answerOnBooking(anyLong(),anyBoolean(), anyLong()))
                .thenThrow(ValidationException.class);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header(HeaderNames.USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
    }
}
