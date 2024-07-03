package ru.practicum.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.CompilationService;
import ru.practicum.util.DataUtils;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PublicCompilationController.class)
class PublicCompilationControllerTest {

    @MockBean
    private CompilationService compilationService;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Test get compilations functionality")
    void givenParams_whenGetCompilations_thenSuccessResponse() throws Exception {
        List<CompilationDto> response = List.of(DataUtils.getCompilationDto());
        BDDMockito.given(compilationService.getAll(any(Boolean.class), any(Pageable.class)))
                .willReturn(response);

        ResultActions result = mvc.perform(get("/compilations")
                        .param("pinned", "false")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        result
                .andDo(print())
                .andExpect(jsonPath("$.[0].id", notNullValue()));
    }

    @Test
    @DisplayName("Test get compilation by id functionality")
    void givenCompId_whenGetCompilation_thenSuccessResponse() throws Exception {
        BDDMockito.given(compilationService.getById(anyLong()))
                .willReturn(DataUtils.getCompilationDto());

        ResultActions result = mvc.perform(get("/compilations/{compId}", 1L))
                .andExpect(status().isOk());

        result
                .andDo(print())
                .andExpect(jsonPath("$.id", notNullValue()));
    }
}