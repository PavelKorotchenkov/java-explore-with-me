package ru.practicum.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.participation.ParticipationRequestDto;
import ru.practicum.service.ParticipationService;
import ru.practicum.util.DataUtils;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PrivateParticipationController.class)
class PrivateParticipationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ParticipationService participationService;

    @Test
    @DisplayName("Test get user requests functionality")
    void givenUserId_whenGetUserRequests_thenSuccessResponse() throws Exception {
        BDDMockito.given(participationService.getAllByRequesterId(anyLong()))
                .willReturn(DataUtils.getParticipationRequestDtoList());

        mvc.perform(get("/users/{userId}/requests", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", notNullValue()));
    }

    @Test
    @DisplayName("Test create request functionality")
    void givenUserIdAndEventId_whenCreateRequest_thenCreatedResponse() throws Exception {
        ParticipationRequestDto response = DataUtils.getParticipationRequestDtoList().get(0);
        BDDMockito.given(participationService.create(anyLong(), anyLong()))
                .willReturn(response);

        mvc.perform(post("/users/{userId}/requests", 1L)
                        .param("eventId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @DisplayName("Test cancel request functionality")
    void given_userIdAndRequestId_whenCancelRequest_thenSuccessResponse() throws Exception {
        ParticipationRequestDto response = DataUtils.getParticipationRequestDtoList().get(0);
        BDDMockito.given(participationService.cancel(anyLong(), anyLong()))
                .willReturn(response);

        mvc.perform(patch("/users/{userId}/requests/{requestId}/cancel", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()));
    }
}