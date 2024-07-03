package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.client.Client;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.PublicGetEventParamsDto;
import ru.practicum.service.CommentService;
import ru.practicum.service.EventService;
import ru.practicum.util.DataUtils;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PublicEventController.class)
class PublicEventControllerTest {

    @MockBean
    private Client client;

    @MockBean
    private EventService eventService;
    @MockBean

    private CommentService commentService;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Test get all functionality")
    void givenParams_whenGetAll_thenSuccessResponse() throws Exception {
        List<EventShortDto> response = List.of(DataUtils.getEventShortDto());
        BDDMockito.given(eventService.getAll(any(PublicGetEventParamsDto.class)))
                .willReturn(response);
        BDDMockito.doNothing().when(client).saveStats(DataUtils.getStatsRequestDto());

        ResultActions result = mvc.perform(get("/events")
                .param("text", "text")
                .param("categories", "1")
                .param("paid", "false")
                .param("rangeStart", "2024-01-01 10:10:10")
                .param("rangeEnd", "2025-01-01 10:10:10")
                .param("onlyAvailable", "false")
                .param("sort", "EVENT_DATE")
                .param("from", "0")
                .param("size", "10"));

        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", notNullValue()));
    }

    @Test
    @DisplayName("Test get by id functionality")
    void getById() throws Exception {
        EventFullDto response = DataUtils.getEventFullDto();
        BDDMockito.given(eventService.getByIdPublic(anyLong()))
                .willReturn(response);
        BDDMockito.doNothing().when(client).saveStats(DataUtils.getStatsRequestDto());

        ResultActions result = mvc.perform(get("/events/{id}", 1L));

        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @DisplayName("Test get comments functionality")
    void givenParams_whenGetComments_thenSuccessResponse() throws Exception {
        BDDMockito.given(commentService.getAll(anyLong(), any(Pageable.class)))
                .willReturn(DataUtils.getCommentsShort());

        ResultActions result = mvc.perform(get("/events/{id}/comments", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", notNullValue()));
    }

    @Test
    @DisplayName("Test get comment by id functionality")
    void givenEventIdAndCommentId_whenGetCommentById_thenSuccessResponse() throws Exception {
        BDDMockito.given(commentService.getCommentById(anyLong(), anyLong()))
                .willReturn(DataUtils.getCommentsShort().get(0));

        ResultActions result = mvc.perform(get("/events/{id}/comments/{commentId}", 1L, 1L))
                .andExpect(status().isOk());

        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()));
    }
}