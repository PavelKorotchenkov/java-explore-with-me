package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.service.CommentService;
import ru.practicum.service.EventService;
import ru.practicum.util.DataUtils;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminEventController.class)
class AdminEventControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;
    @MockBean
    private CommentService commentService;

    @Test
    @DisplayName("Test get events functionality")
    void givenParams_whenGetEvents_thenSuccessResponse() throws Exception {
        BDDMockito.given(eventService.getAll(DataUtils.getAdminEventParamsDto()))
                .willReturn(List.of(DataUtils.getEventFullDto()));

        ResultActions result = mvc.perform(get("/admin/events")
                .param("users", "1")
                .param("states", "PUBLISHED")
                .param("categories", "1")
                .param("rangeStart", "2024-10-10 10:10:10")
                .param("rangeEnd", "2025-10-10 10:10:10")
                .param("from", "0")
                .param("size", "10"));

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", notNullValue()));
    }

    @Test
    @DisplayName("Test update events functionality")
    void givenEventIdAndRequest_whenUpdateEvent_thenSuccessResponse() throws Exception {
        long id = 1L;
        UpdateEventAdminRequest request = DataUtils.getUpdateEventAdminRequest();

        BDDMockito.given(eventService.updateByAdmin(anyLong(),any(UpdateEventAdminRequest.class)))
                .willReturn(DataUtils.getEventFullDto());

        mvc.perform(patch("/admin/events/{eventId}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test update events with incorrect description length functionality")
    void givenEventIdAndRequestDescriptionLengthLessThan20Chars_whenUpdateEvent_thenBadRequestResponse() throws Exception {
        long id = 1L;
        UpdateEventAdminRequest request = DataUtils.getUpdateEventAdminRequest();
        request.setDescription("incorrect length");

        mvc.perform(patch("/admin/events/{eventId}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());

        verify(eventService,never()).updateByAdmin(anyLong(), any(UpdateEventAdminRequest.class));
    }

    @Test
    @DisplayName("Test delete functionality")
    void givenEventIdAndCommentId_whenDeleteComment_thenNoContentStatusReturned() throws Exception {
        long eventId = 1L;
        long commentId = 1L;

        BDDMockito.doNothing().when(commentService).deleteByAdmin(anyLong(), anyLong());

        mvc.perform(delete("/admin/events/{eventId}/comments/{commentId}", eventId, commentId))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteByAdmin(eventId, commentId);
    }
}