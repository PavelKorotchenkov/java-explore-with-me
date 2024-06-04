package ru.practicum.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class OffsetPageRequest extends PageRequest {
	protected OffsetPageRequest(int page, int size, Sort sort) {
		super(page, size, sort);
	}

	public static PageRequest createPageRequest(Integer offset, Integer size) {
		return PageRequest.of(offset, size);
	}

	public static PageRequest createPageRequest(Integer offset, Integer size, Sort by) {
		return PageRequest.of(offset, size, by);
	}

}
