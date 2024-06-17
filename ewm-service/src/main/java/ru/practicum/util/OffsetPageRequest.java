package ru.practicum.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


public class OffsetPageRequest extends PageRequest {

    private final int offset;

    protected OffsetPageRequest(int from, int size, Sort sort) {
        super(from / size, size, sort);
        this.offset = from;
    }

    public static PageRequest createPageRequest(Integer from, Integer size) {
        return new OffsetPageRequest(from, size, Sort.unsorted());
    }

    public static PageRequest createPageRequest(Integer from, Integer size, Sort sort) {
        return new OffsetPageRequest(from, size, sort);
    }

    @Override
    public long getOffset() {
        return this.offset;
    }

}
