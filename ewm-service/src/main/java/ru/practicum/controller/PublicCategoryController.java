package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.CategoryService;
import ru.practicum.util.OffsetPageRequest;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
public class PublicCategoryController {
	private final CategoryService categoryService;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") int from,
										   @RequestParam(defaultValue = "10") int size) {
		log.info("Request for a page of categories with params: from: {}, size: {}", from, size);
		Pageable page = OffsetPageRequest.createPageRequest(from, size);
		List<CategoryDto> result = categoryService.getCategories(page);
		log.info("Response for a page of categories, found {} categories", result.size());
		return result;
	}

	@GetMapping("/{catId}")
	@ResponseStatus(HttpStatus.OK)
	public CategoryDto getCategories(@PathVariable Long catId) {
		log.info("Request for the category with id: {}", catId);
		CategoryDto result = categoryService.getCategoryById(catId);
		log.info("Response for the category: {}", result);
		return result;
	}
}
