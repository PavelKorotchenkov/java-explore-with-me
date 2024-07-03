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
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.service.CompilationService;
import ru.practicum.util.DataUtils;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminCompilationController.class)
class AdminCompilationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompilationService compilationService;

    @Test
    @DisplayName("Test post new compilation functionality")
    void givenNewCompilation_whenPostNewCompilation_thenSuccessResponse() throws Exception {
        NewCompilationDto newCompilationDto = DataUtils.getNewCompilationDto();
        CompilationDto compilationDto = DataUtils.getCompilationDto();

        when(compilationService.create(newCompilationDto)).thenReturn(compilationDto);

        ResultActions result = mvc.perform(post("/admin/compilations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCompilationDto)));
        result
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(1L), Long.class));
    }

    @Test
    @DisplayName("Test post new compilation with empty title functionality")
    void givenNewCompilationWithEmptyTitle_whenPostNewCompilation_thenBadRequestResponse() throws Exception {
        NewCompilationDto newCompilationDto = DataUtils.getNewCompilationDto();
        newCompilationDto.setTitle("");

        ResultActions result = mvc.perform(post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCompilationDto)))
                .andExpect(status().is4xxClientError());

        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Validation failed")));
        verify(compilationService, never()).create(any(NewCompilationDto.class));
    }

    @Test
    @DisplayName("Test post new compilation with name length > 50 functionality")
    void givenNewCompilationWithNameHasMoreThanFiftyChars_whenPostNewCompilation_thenBadRequestResponse() throws Exception {
        NewCompilationDto newCompilationDto = DataUtils.getNewCompilationDto();
        newCompilationDto.setTitle("PrettyLongCompilationTitleDontYouThinkSoThatWouldNotWork");

        ResultActions result = mvc.perform(post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCompilationDto)))
                .andExpect(status().is4xxClientError());

        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Validation failed")));
        verify(compilationService, never()).create(any(NewCompilationDto.class));
    }

    @Test
    @DisplayName("Test delete functionality")
    void givenCompId_whenDelete_thenNoContentResponse() throws Exception {
        long id = 1L;
        BDDMockito.doNothing().when(compilationService).delete(anyLong());
        mvc.perform(delete("/admin/compilations/{compId}", id))
                .andExpect(status().isNoContent());
        verify(compilationService, times(1)).delete(id);
    }

    @Test
    @DisplayName("Test update functionality")
    void givenCompIdAndUpdateCompRequest_whenUpdate_thenSuccessResponse() throws Exception {
        long id = 1L;
        NewCompilationDto newCompilationDto = DataUtils.getNewCompilationDto();
        CompilationDto response = DataUtils.getCompilationDto();

        BDDMockito.given(compilationService.update(anyLong(), any(UpdateCompilationRequest.class))).willReturn(response);

        ResultActions result = mvc.perform(patch("/admin/compilations/{compId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCompilationDto)));
        result
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Compilation")));
    }
}