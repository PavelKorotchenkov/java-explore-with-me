package ru.practicum.dto.category;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.model.Category;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryDtoMapper {

	Category newCategoryDtoToCategory(NewCategoryDto newCategoryDto);

	CategoryDto categoryToCategoryDto(Category category);
}
