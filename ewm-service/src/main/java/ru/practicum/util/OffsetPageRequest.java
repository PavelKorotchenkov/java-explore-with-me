package ru.practicum.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class OffsetPageRequest extends PageRequest {

	protected OffsetPageRequest(int page, int size, Sort sort) {
		super(page, size, sort);
	}

	public static PageRequest createPageRequest(Integer from, Integer size) {
		return PageRequest.of(from / size, size);
	}
	public static PageRequest createPageRequest(Integer from, Integer size, Sort sort) {
		return PageRequest.of(from / size, size, sort);
	}
}
