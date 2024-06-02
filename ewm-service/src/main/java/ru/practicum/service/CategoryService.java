package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
	CategoryDto addNewCategory(NewCategoryDto newCategoryDto);

	void deleteCategory(long catId);

	CategoryDto updateCategory(NewCategoryDto newCategoryDto, long catId);

	List<CategoryDto> getCategories(Pageable pageable);

	CategoryDto getCategoryById(long catId);
}
