package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {

    private final ObjectMapper mapper;

    private final MockMvc mvc;

    @MockBean
    BookingService bookingService;

    Booking booking;

    BookingDto bookingDto;

    @BeforeAll
    public void setUp() {

        booking = new Booking();
        booking.setId(1);
        booking.setItemId(1);
        booking.setStart(LocalDateTime.of(2022, 10, 10, 10, 10, 10));
        booking.setEnd(LocalDateTime.of(2050, 10, 10, 10, 10, 10));
        booking.setBookingApproved("APPROVED");

        bookingDto = new BookingDto();
        bookingDto.setId(1);
        bookingDto.setItem(new ItemDto());
        bookingDto.setBooker(new UserDto());
        bookingDto.setStart(LocalDateTime.of(2022, 10, 10, 10, 10, 10));
        bookingDto.setEnd(LocalDateTime.of(2050, 10, 10, 10, 10, 10));
        bookingDto.setStatus("APPROVED");

    }

    @Test
    public void createBooking() throws Exception {

        when(bookingService.createBooking(anyLong(), any(Booking.class)))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(booking))
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().toString()))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().toString()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus()));

    }

    @Test
    public void setApprove() throws Exception {

        when(bookingService.setApprove(anyBoolean(), anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(booking))
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().toString()))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().toString()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus()));

    }

    @Test
    public void getBooking() throws Exception {

        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().toString()))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().toString()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus()));

    }

    @Test
    public void getAllBookings() throws Exception {

        when(bookingService.getAllBookings(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingDto.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(bookingDto.getEnd().toString()))
                .andExpect(jsonPath("$[0].status").value(bookingDto.getStatus()));

    }

    @Test
    public void getOwnerBookings() throws Exception {

        when(bookingService.getOwnerBookings(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingDto.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(bookingDto.getEnd().toString()))
                .andExpect(jsonPath("$[0].status").value(bookingDto.getStatus()));

    }


}
