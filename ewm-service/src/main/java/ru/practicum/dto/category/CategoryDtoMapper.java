package ru.practicum.dto.category;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.model.Category;

@Mapper
public interface CategoryDtoMapper {
	CategoryDtoMapper INSTANCE = Mappers.getMapper(CategoryDtoMapper.class);

	Category newCategoryDtoToCategory(NewCategoryDto newCategoryDto);

	CategoryDto categoryToCategoryDto(Category category);
}
