package ru.practicum.dto.event;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AdminGetEventParamsDto {
	private final List<Long> users;
	private final List<String> states;
	private final List<Long> categories;
	private final String rangeStart;
	private final String rangeEnd;
}
