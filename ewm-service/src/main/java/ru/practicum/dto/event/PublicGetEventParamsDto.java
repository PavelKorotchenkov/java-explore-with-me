package ru.practicum.dto.event;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PublicGetEventParamsDto {
    private final String text;
    private final List<Long> categories;
    private final Boolean paid;
    private final String rangeStart;
    private final String rangeEnd;
    private final boolean onlyAvailable;
    private final String sort;
    private final int from;
    private final int size;
}
