package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.participation.EventRequestStatusUpdateRequest;
import ru.practicum.dto.participation.EventRequestStatusUpdateResult;
import ru.practicum.service.CommentService;
import ru.practicum.service.EventService;
import ru.practicum.util.DataUtils;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrivateEventController.class)
class PrivateEventControllerTest {

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
    void givenId_whenGetEvents_thenSuccessResponse() throws Exception {
        long userId = 1L;

        BDDMockito.given(eventService.getAllByInitiator(userId, Pageable.unpaged()))
                .willReturn(List.of(DataUtils.getEventShortDto()));

        ResultActions result = mvc.perform(get("/users/{userId}/events", userId)
                .param("from", "0")
                .param("size", "10"));

        result
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test post new event functionality")
    void givenUserIdAndNewEventDto_whenPostNewEvent_thenCreatedResponse() throws Exception {
        long userId = 1L;
        NewEventDto newEventDto = DataUtils.getNewEventDto();

        BDDMockito.given(eventService.create(anyLong(), any(NewEventDto.class)))
                .willReturn(DataUtils.getEventFullDto());

        ResultActions result = mvc.perform(post("/users/{userId}/events", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEventDto)));

        result
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Test post new event with incorrect title with < 3 chars functionality")
    void givenNewEventTitleLengthLessThan3_whenPostNewEvent_thenBadRequestResponse() throws Exception {
        long userId = 1L;

        NewEventDto newEventDto = DataUtils.getNewEventDto();
        newEventDto.setTitle("FU");

        mvc.perform(post("/users/{userId}/events", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test get full event info functionality")
    void givenUserIdAndEventId_whenGetEventFullInfo_thenSuccessResponse() throws Exception {
        long userId = 1L;
        long eventId = 1L;

        BDDMockito.given(eventService.getOneByInitiator(anyLong(), anyLong()))
                .willReturn(DataUtils.getEventFullDto());

        ResultActions result = mvc.perform(get("/users/{userId}/events/{eventId}", userId, eventId));

        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.annotation", is("Annotation needs twenty chars")));
    }

    @Test
    @DisplayName("Test update event functionality")
    void givenUserIdAndEventIdAndRequest_whenUpdateEvent_thenSuccessResponse() throws Exception {
        long userId = 1L;
        long eventId = 1L;
        UpdateEventUserRequest request = DataUtils.getUpdateEventUserRequest();

        BDDMockito.given(eventService.updateByInitiator(anyLong(), anyLong(), any(UpdateEventUserRequest.class)))
                .willReturn(DataUtils.getEventFullDto());

        ResultActions result = mvc.perform(patch("/users/{userId}/events/{eventId}", userId, eventId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test get event participations request functionality")
    void givenUserIdAndEventId_whenGetEventParticipationRequests_thenSuccessResponse() throws Exception {
        long userId = 1L;
        long eventId = 1L;

        BDDMockito.given(eventService.getRequests(anyLong(), anyLong()))
                .willReturn(DataUtils.getParticipationRequestDtoList());

        ResultActions result = mvc.perform(get("/users/{userId}/events/{eventId}/requests", userId, eventId));

        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", notNullValue()));
    }

    @Test
    @DisplayName("Test update participation requests status")
    void givenUserIdAndEventIdAndRequest_whenUpdateParticipationRequestStatus_thenSuccessResponse() throws Exception {
        long userId = 1L;
        long eventId = 1L;

        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();
        request.setRequestIds(List.of(1L));
        request.setStatus("PUBLISHED");

        EventRequestStatusUpdateResult result = DataUtils.getEventRequestStatusUpdateResult();
        result.setConfirmedRequests(List.of(DataUtils.getParticipationRequestDtoList().get(0)));

        BDDMockito.given(eventService.updateRequestStatus(anyLong(), anyLong(), any(EventRequestStatusUpdateRequest.class)))
                .willReturn(result);

        ResultActions response = mvc.perform(patch("/users/{userId}/events/{eventId}/requests", userId, eventId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        response
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.confirmedRequests[0].id", notNullValue()));
    }

    @Test
    @DisplayName("Test post comment functionality")
    void givenUserIdAndEventIdAndComment_whenPostComment_thenCreatedResponse() throws Exception {
        NewCommentDto newCommentDto = DataUtils.getNewCommentDto();
        CommentDto commentDto = DataUtils.getCommentDtoOne();

        BDDMockito.given(commentService.create(any(NewCommentDto.class), anyLong(), anyLong()))
                .willReturn(commentDto);

        ResultActions result = mvc.perform(post("/users/{userId}/events/{eventId}/comments", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCommentDto)));

        result
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Test update comment functionality")
    void givenUserIdAndEventIdAndCommentIdAndNewComment_whenUpdateComment_thenSuccessResponse()
            throws Exception {
        NewCommentDto newCommentDto = DataUtils.getNewCommentDto();
        CommentDto commentDto = DataUtils.getCommentDtoOne();

        BDDMockito.given(commentService.updateByAuthor(any(NewCommentDto.class), anyLong(), anyLong(), anyLong()))
                .willReturn(commentDto);

        ResultActions result = mvc.perform(patch("/users/{userId}/events/{eventId}/comments/{commentId}", 1L, 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCommentDto)));

        result
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test delete comment functionality")
    void givenUserIdAndEventIdAndCommentId_whenDeleteComment_thenServiceDeleteByAuthorMethodIsUsed() throws Exception {
        BDDMockito.doNothing().when(commentService).deleteByAuthor(anyLong(), anyLong(), anyLong());
        mvc.perform(delete("/users/{userId}/events/{eventId}/comments/{commentId}", 1L, 1L, 1L))
                .andExpect(status().isNoContent());
        verify(commentService, times(1)).deleteByAuthor(anyLong(), anyLong(), anyLong());
    }
}