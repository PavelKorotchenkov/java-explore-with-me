package ru.practicum.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.CategoryService;
import ru.practicum.util.DataUtils;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PublicCategoryController.class)
class PublicCategoryControllerTest {

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Test get categories functionality")
    void givenParams_whenGetCategories_thenSuccessResponse() throws Exception {
        List<CategoryDto> response = List.of(DataUtils.getCategoryDto());
        BDDMockito.given(categoryService.getAll(any(PageRequest.class)))
                .willReturn(response);

        ResultActions result = mvc.perform(get("/categories")
                .param("from", "0")
                .param("size", "10"));

        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", notNullValue()));
    }

    @Test
    @DisplayName("Test get category by id functionality")
    void givenCatId_whenGetCategory_thenSuccessResponse() throws Exception {
        CategoryDto response = DataUtils.getCategoryDto();
        BDDMockito.given(categoryService.getById(anyLong()))
                .willReturn(response);

        ResultActions result = mvc.perform(get("/categories/{catId}", 1L));

        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()));
    }
}