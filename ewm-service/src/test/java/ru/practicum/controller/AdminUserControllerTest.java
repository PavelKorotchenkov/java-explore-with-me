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
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.service.UserService;
import ru.practicum.util.DataUtils;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
class AdminUserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Test get users functionality")
    void givenParams_whenGetUsers_thenSuccessResponse() throws Exception {
        List<UserDto> response = List.of(DataUtils.getUserDto());

        BDDMockito.given(userService.getAll(anyList(), any(Pageable.class)))
                .willReturn(response);

        ResultActions result = mvc.perform(get("/admin/users")
                .param("ids", "1")
                .param("from", "0")
                .param("size", "10"));

        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", notNullValue()))
                .andExpect(jsonPath("$.[0].id", is(1)));
    }

    @Test
    @DisplayName("Test create new user functionality")
    void givenNewUser_whenCreateNewUser_thenSuccessResponse() throws Exception {
        NewUserRequest request = DataUtils.getNewUserRequest();
        BDDMockito.given(userService.create(any(NewUserRequest.class)))
                .willReturn(DataUtils.getUserDto());

        ResultActions result = mvc.perform(post("/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        result
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Test create new user with incorrect email with < 6 chars functionality")
    void givenIncorrectEmailLength_whenCreateNewUser_thenBadRequestResponse() throws Exception {
        NewUserRequest request = DataUtils.getNewUserRequest();
        request.setEmail("@a.ru");
        mvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test create new user with incorrect email without @ symbol functionality")
    void givenIncorrectEmailAt_whenCreateNewUser_thenBadRequestResponse() throws Exception {
        NewUserRequest request = DataUtils.getNewUserRequest();
        request.setEmail("email.com");
        mvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test create new user with incorrect email is blank functionality")
    void givenIncorrectEmailBlank_whenCreateNewUser_thenBadRequestResponse() throws Exception {
        NewUserRequest request = DataUtils.getNewUserRequest();
        request.setEmail("");
        mvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test create new user with incorrect name length < 2 chars functionality")
    void givenIncorrectName_whenCreateNewUser_thenBadRequestResponse() throws Exception {
        NewUserRequest request = DataUtils.getNewUserRequest();
        request.setName("A");
        mvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test create new user with incorrect name is blank functionality")
    void givenIncorrectBlankName_whenCreateNewUser_thenBadRequestResponse() throws Exception {
        NewUserRequest request = DataUtils.getNewUserRequest();
        request.setName("");
        mvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test delete functionality")
    void givenUserId_whenDeleteUser_thenUserServiceDeleteMethodIsUsed() throws Exception {
        long id = 1L;

        BDDMockito.doNothing().when(userService).delete(anyLong());

        mvc.perform(delete("/admin/users/{userId}", id))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(id);
    }
}