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
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.service.CategoryService;
import ru.practicum.util.DataUtils;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminCategoryController.class)
class AdminCategoryControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    @DisplayName("Test create new category functionality")
    void givenNewCategory_whenAddNew_thenSuccessResponse() throws Exception {
        NewCategoryDto newCategoryDto = DataUtils.getNewCategoryDto();
        CategoryDto categoryResponseDto = DataUtils.getCategoryDto();

        when(categoryService.create(newCategoryDto)).thenReturn(categoryResponseDto);

        ResultActions result = mvc.perform(post("/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategoryDto)));

        result
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Category name")));
    }

    @Test
    @DisplayName("Test create new category with empty name functionality")
    void givenNewCategoryWithEmptyName_whenAddNew_thenStatusBadResponse() throws Exception {
        NewCategoryDto newCategoryDto = DataUtils.getNewCategoryDto();
        newCategoryDto.setName("");

        ResultActions result = mvc.perform(post("/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategoryDto)));

        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Validation failed")));

        verify(categoryService, never()).create(any(NewCategoryDto.class));
    }

    @Test
    @DisplayName("Test create new category with name length > 50 functionality")
    void givenNewCategoryWithNameHasMoreThanFiftyChars_whenAddNew_thenStatusBadResponse() throws Exception {
        NewCategoryDto newCategoryDto = DataUtils.getNewCategoryDto();
        String fiftyOneCharsLength = "PrettyLongCategoryNameDontYouThinkSoThatWouldNotWork";
        newCategoryDto.setName(fiftyOneCharsLength);

        ResultActions result = mvc.perform(post("/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategoryDto)));

        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Validation failed")));
        verify(categoryService, never()).create(any(NewCategoryDto.class));
    }

    @Test
    @DisplayName("Test delete category functionality")
    void givenCatId_whenDelete_thenSuccessResponse() throws Exception {
        long id = 1L;
        BDDMockito.doNothing().when(categoryService).delete(anyLong());

        mvc.perform(delete("/admin/categories/{catId}", id))
                .andExpect(status().is2xxSuccessful());

        verify(categoryService, times(1)).delete(id);
    }

    @Test
    void givenNewCategoryAndCatId_whenUpdate_thenSuccessResponse() throws Exception {
        long id = 1L;
        NewCategoryDto newCategoryDto = DataUtils.getNewCategoryDto();
        CategoryDto response = DataUtils.getCategoryDto();

        BDDMockito.given(categoryService.update(any(NewCategoryDto.class), anyLong()))
                .willReturn(response);

        ResultActions result = mvc.perform(patch("/admin/categories/{catId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategoryDto)));

        result
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name", is("Category name")));
    }
}