package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {

    private final ObjectMapper mapper;

    private final MockMvc mvc;

    @MockBean
    ItemService itemService;

    Item item;

    ItemDto itemDto;

    @BeforeAll
    public void setUp() {

        item = new Item();
        item.setId(1);
        item.setName("Дрель");
        item.setDescription("Супер дрель");
        item.setOwnerId(1);
        item.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("Дрель");
        itemDto.setDescription("Супер дрель");
        itemDto.setOwnerId(1);
        itemDto.setAvailable(true);

    }

    @Test
    public void createItem() throws Exception {

        when(itemService.create(any(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.ownerId").value(itemDto.getOwnerId()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

    }

    @Test
    public void updateItem() throws Exception {

        when(itemService.update(any(ItemDto.class), anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.ownerId").value(itemDto.getOwnerId()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

    }

    @Test
    public void getUserItems() throws Exception {

        when(itemService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(ItemMapper.toItemBookingDto(item,
                        null,
                        null,
                        List.of(new CommentDto())
                )));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].ownerId").value(itemDto.getOwnerId()))
                .andExpect(jsonPath("$[0].requestId").value(itemDto.getRequestId()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));

    }

    @Test
    public void getItem() throws Exception {

        when(itemService.get(anyLong(), anyLong()))
                .thenReturn(ItemMapper.toItemBookingDto(item,
                        null,
                        null,
                        List.of(new CommentDto())
                ));

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.ownerId").value(itemDto.getOwnerId()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

    }

    @Test
    public void findByText() throws Exception {

        when(itemService.findByText(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .param("text", "ololo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].ownerId").value(itemDto.getOwnerId()))
                .andExpect(jsonPath("$[0].requestId").value(itemDto.getRequestId()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));

    }


}
