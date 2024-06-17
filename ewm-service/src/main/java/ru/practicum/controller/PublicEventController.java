package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.Client;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.comment.CommentShort;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.PublicGetEventParamsDto;
import ru.practicum.service.CommentService;
import ru.practicum.service.EventService;
import ru.practicum.util.OffsetPageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class PublicEventController {

    public static final String APP = "ewm-main-service";
    private final Client client;
    private final EventService eventService;
    private final CommentService commentService;

    @Transactional
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAll(@RequestParam(required = false) String text,
                                      @RequestParam(required = false) List<Long> categories,
                                      @RequestParam(required = false) Boolean paid,
                                      @RequestParam(required = false) String rangeStart,
                                      @RequestParam(required = false) String rangeEnd,
                                      @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                      @RequestParam(required = false) String sort,
                                      @RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "10") int size,
                                      HttpServletRequest request) {
        log.info("Request for the list of public events with params: text: {}, categories: {}, paid: {}, rangeStart: {}, " +
                        "rangeEnd: {}, onlyAvailable: {}, sort: {}, from: {}, size: {}", text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size);
        PublicGetEventParamsDto params = createParams(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size);
        List<EventShortDto> result = eventService.getAll(params);
        log.info("Response for the list of public events: found {} events", result.size());
        saveStats(request);
        log.info("Saving stats for /events");
        return result;
    }

    @Transactional
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getById(@PathVariable Long id,
                                HttpServletRequest request) {
        log.info("Request for getting event with id: {}", id);
        EventFullDto response = eventService.getByIdPublic(id);
        log.info("Response for getting event: {}", response);

        saveStats(request);
        log.info("Saving stats for /events/{} ", id);
        return response;
    }

    @GetMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentShort> getComments(@PathVariable Long id,
                                          @RequestParam(defaultValue = "0") @Min(0) int from,
                                          @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Request for all comments to event: {}", id);
        Pageable page = OffsetPageRequest.createPageRequest(from, size, Sort.by(Sort.Direction.DESC, "CreatedOn"));
        List<CommentShort> response = commentService.getAll(id, page);
        log.info("Request for all comments to event: {}", id);
        return response;
    }

    @GetMapping("/{id}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentShort getCommentById(@PathVariable Long id,
                                       @PathVariable Long commentId) {
        log.info("Request for comment with id: {} to event: {}", commentId, id);
        CommentShort response = commentService.getCommentById(id, commentId);
        log.info("Request for comment: {} to event: {}", commentId, id);
        return response;
    }

    private PublicGetEventParamsDto createParams(String text, List<Long> categories, Boolean paid, String rangeStart,
                                                 String rangeEnd, boolean onlyAvailable, String sort, int from, int size) {
        return PublicGetEventParamsDto.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();
    }

    private void saveStats(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        client.saveStats(new StatsRequestDto(APP, uri, ip, LocalDateTime.now()));
    }
}
