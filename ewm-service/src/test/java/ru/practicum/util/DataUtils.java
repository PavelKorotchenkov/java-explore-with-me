package ru.practicum.util;

import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentShort;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.*;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.dto.participation.EventRequestStatusUpdateResult;
import ru.practicum.dto.participation.ParticipationRequestDto;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.enums.EventState;
import ru.practicum.enums.ParticipationRequestStatus;
import ru.practicum.model.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataUtils {
    public static Category getCategoryTransient() {
        Category category = new Category();
        category.setName("Category name");

        return category;
    }

    public static Category getCategoryPersisted() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Category name");

        return category;
    }

    public static NewCategoryDto getNewCategoryDto() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("Category name");

        return newCategoryDto;
    }

    public static CategoryDto getCategoryDto() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Category name");

        return categoryDto;
    }

    public static User getUserInitiatorTransient() {
        User initiator = new User();
        initiator.setName("User name");
        initiator.setEmail("User@email.com");

        return initiator;
    }

    public static User getUserInitiatorPersisted() {
        User initiator = new User();
        initiator.setId(1L);
        initiator.setName("User name");
        initiator.setEmail("User@email.com");

        return initiator;
    }

    public static UserDto getUserDto() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("User name");
        userDto.setEmail("User@email.com");

        return userDto;
    }

    public static NewUserRequest getNewUserRequest() {
        NewUserRequest request = new NewUserRequest();
        request.setName("User name");
        request.setEmail("User@email.com");
        return request;
    }

    public static User getUserAuthorTransient() {
        User author = new User();
        author.setName("Commentator name");
        author.setEmail("Commentator@email.com");

        return author;
    }

    public static User getUserAuthorPersisted() {
        User author = new User();
        author.setId(2L);
        author.setName("Commentator name");
        author.setEmail("Commentator@email.com");

        return author;
    }

    public static User getUserRequesterTransient() {
        User author = new User();
        author.setName("Requester name");
        author.setEmail("Requester@email.com");

        return author;
    }

    public static User getUserRequesterPersisted() {
        User author = new User();
        author.setId(3L);
        author.setName("Requester name");
        author.setEmail("Requester@email.com");

        return author;
    }

    private static UserShortDto getUserShortDtoInitiatorPersisted() {
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(1L);
        userShortDto.setName("User name");
        return userShortDto;
    }

    public static Location getLocationTransient() {
        Location location = new Location();
        location.setLat(12.3f);
        location.setLon(22.3f);

        return location;
    }

    public static Location getLocationPersisted() {
        Location location = new Location();
        location.setId(1L);
        location.setLat(12.3f);
        location.setLon(22.3f);

        return location;
    }

    public static Event getEventTransient(Category category, User initiator, Location location) {
        return Event.builder()
                .annotation("Annotation needs twenty chars")
                .category(category)
                .createdOn(LocalDateTime.now())
                .description("Description needs twenty chars")
                .eventDate(LocalDateTime.now().plusDays(3))
                .initiator(initiator)
                .location(location)
                .paid(false)
                .participantLimit(10)
                .publishedOn(LocalDateTime.now())
                .requestModeration(false)
                .state(EventState.PUBLISHED)
                .title("Event title")
                .build();
    }

    public static Event getEventPersisted(Category category, User initiator, Location location) {
        return Event.builder()
                .id(1L)
                .annotation("Annotation needs twenty chars")
                .category(category)
                .createdOn(LocalDateTime.now())
                .description("Description needs twenty chars")
                .eventDate(LocalDateTime.now().plusDays(3))
                .initiator(initiator)
                .location(location)
                .paid(false)
                .participantLimit(10)
                .publishedOn(LocalDateTime.now())
                .requestModeration(false)
                .state(EventState.PUBLISHED)
                .title("Event title")
                .build();
    }

    public static EventFullDto getEventFullDto() {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setAnnotation("Annotation needs twenty chars");
        eventFullDto.setCategory(getCategoryDto());
        eventFullDto.setConfirmedRequests(1);
        eventFullDto.setCreatedOn("2024-10-10 10:10:10");
        eventFullDto.setDescription("Description needs twenty chars");
        eventFullDto.setEventDate("2025-10-10 10:10:10");
        eventFullDto.setId(1L);
        eventFullDto.setInitiator(getUserShortDtoInitiatorPersisted());
        eventFullDto.setLocation(getLocationPersisted());
        eventFullDto.setPaid(false);
        eventFullDto.setParticipantLimit(10);
        eventFullDto.setPublishedOn("2024-10-10 12:10:10");
        eventFullDto.setRequestModeration(false);
        eventFullDto.setState("PUBLISHED");
        eventFullDto.setTitle("Event title");
        eventFullDto.setViews(1);
        return eventFullDto;
    }

    public static EventShortDto getEventShortDto() {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(1L);
        eventShortDto.setDescription("Description needs twenty chars");
        eventShortDto.setAnnotation("Annotation needs twenty chars");
        eventShortDto.setCategory(getCategoryDto());
        eventShortDto.setConfirmedRequests(1);
        eventShortDto.setEventDate("2025-10-10 10:10:10");
        eventShortDto.setInitiator(getUserShortDtoInitiatorPersisted());
        eventShortDto.setPaid(false);
        eventShortDto.setTitle("Event title");
        eventShortDto.setViews(1);
        eventShortDto.setCommentsCount(1L);
        return eventShortDto;
    }

    public static NewEventDto getNewEventDto() {
        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setAnnotation("Annotation needs twenty chars");
        newEventDto.setCategory(1L);
        newEventDto.setDescription("Description needs twenty chars");
        newEventDto.setEventDate("2025-10-10 10:10:10");
        newEventDto.setPaid(false);
        newEventDto.setTitle("Event title");
        newEventDto.setLocation(new LocationDto());
        newEventDto.setPaid(false);
        newEventDto.setParticipantLimit(10);
        newEventDto.setRequestModeration(false);
        newEventDto.setTitle("Title");
        return newEventDto;
    }

    public static UpdateEventAdminRequest getUpdateEventAdminRequest() {
        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setAnnotation("Annotation update need twenty chars");
        request.setCategory(1L);
        request.setDescription("Description update need twenty chars");
        request.setEventDate("2025-10-10 10:10:10");
        request.setLocation(new LocationDto());
        request.setPaid(false);
        request.setParticipantLimit(10);
        request.setRequestModeration(false);
        request.setTitle("Event title");
        request.setStateAction("CANCEL_REVIEW");
        return request;
    }

    public static UpdateEventUserRequest getUpdateEventUserRequest() {
        UpdateEventUserRequest request = new UpdateEventUserRequest();
        request.setAnnotation("Annotation update need twenty chars");
        request.setCategory(1L);
        request.setDescription("Description update need twenty chars");
        request.setEventDate("2025-10-10 10:10:10");
        request.setLocation(new LocationDto());
        request.setPaid(false);
        request.setParticipantLimit(10);
        request.setRequestModeration(false);
        request.setStateAction("CANCEL_REVIEW");
        request.setTitle("Event title");
        return request;
    }

    public static Comment getCommentOneTransient(Event event, User author) {
        return Comment.builder()
                .text("Comment1")
                .event(event)
                .author(author)
                .createdOn(LocalDateTime.now().plusDays(5))
                .updated(false)
                .build();
    }

    public static Comment getCommentOnePersisted(Event event, User author) {
        return Comment.builder()
                .id(1L)
                .text("Comment1")
                .event(event)
                .author(author)
                .createdOn(LocalDateTime.now().plusDays(5))
                .updated(false)
                .build();
    }

    public static Comment getUpdatedCommentOnePersisted(Event event, User author) {
        return Comment.builder()
                .id(1L)
                .text("Comment1")
                .event(event)
                .author(author)
                .createdOn(LocalDateTime.now().plusDays(5))
                .updated(true)
                .build();
    }

    public static Comment getCommentTwoTransient(Event event, User author) {
        return Comment.builder()
                .text("Comment2")
                .event(event)
                .author(author)
                .createdOn(LocalDateTime.now().plusDays(5))
                .updated(false)
                .build();
    }

    public static Comment getCommentTwoPersisted(Event event, User author) {
        return Comment.builder()
                .id(2L)
                .text("Comment2")
                .event(event)
                .author(author)
                .createdOn(LocalDateTime.now().plusDays(5))
                .updated(false)
                .build();
    }

    public static NewCommentDto getNewCommentDto() {
        NewCommentDto newCommentDto = new NewCommentDto();
        newCommentDto.setText("Comment1");
        return newCommentDto;
    }

    public static CommentDto getCommentDtoOne() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Comment1");
        commentDto.setEventId(1L);
        commentDto.setAuthorId(1L);
        commentDto.setCreatedOn("10-10-2024 10:10:10");
        commentDto.setUpdated(false);
        return commentDto;
    }

    public static CommentDto getUpdatedCommentDtoOne() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Comment1");
        commentDto.setEventId(1L);
        commentDto.setAuthorId(1L);
        commentDto.setCreatedOn("2024-10-10 10:10:10");
        commentDto.setUpdated(true);
        return commentDto;
    }

    public static List<CommentShort> getCommentsShort() {
        CommentShort commentShort = new CommentShort() {
            @Override
            public long getId() {
                return 1;
            }

            @Override
            public String getText() {
                return "comment";
            }

            @Override
            public String getAuthorName() {
                return "User";
            }

            @Override
            public LocalDateTime getCreatedOn() {
                return LocalDateTime.now();
            }

            @Override
            public boolean isUpdated() {
                return false;
            }
        };

        return List.of(commentShort);
    }

    public static Compilation getCompilationTransient() {
        return Compilation.builder()
                .events(new HashSet<>())
                .pinned(false)
                .title("Compilation")
                .build();
    }

    public static Compilation getCompilationPersisted() {
        return Compilation.builder()
                .id(1L)
                .events(new HashSet<>())
                .pinned(false)
                .title("Compilation")
                .build();
    }

    public static NewCompilationDto getNewCompilationDto() {
        NewCompilationDto newCompilationDto = new NewCompilationDto();
        newCompilationDto.setEvents(Set.of(1L));
        newCompilationDto.setPinned(false);
        newCompilationDto.setTitle("Compilation");
        return newCompilationDto;
    }

    public static CompilationDto getCompilationDto() {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(1L);
        compilationDto.setEvents(Set.of(new EventShortDto()));
        compilationDto.setPinned(false);
        compilationDto.setTitle("Compilation");
        return compilationDto;
    }

    public static UpdateCompilationRequest getUpdateCompilationRequest() {
        UpdateCompilationRequest updateCompilationRequest = new UpdateCompilationRequest();
        updateCompilationRequest.setEvents(new HashSet<>());
        updateCompilationRequest.setPinned(false);
        updateCompilationRequest.setTitle("Compilation");
        return updateCompilationRequest;
    }

    public static ParticipationRequest getParticipationRequestConfirmedTransient(Event event, User requester) {
        return ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .status(ParticipationRequestStatus.CONFIRMED)
                .build();
    }

    public static ParticipationRequest getParticipationRequestConfirmedPersisted(Event event, User requester) {
        return ParticipationRequest.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .status(ParticipationRequestStatus.CONFIRMED)
                .build();
    }

    public static AdminGetEventParamsDto getAdminEventParamsDto() {
        return AdminGetEventParamsDto.builder()
                .users(List.of(1L))
                .states(List.of("PUBLISHED"))
                .categories(List.of(1L))
                .rangeStart("2024-10-10 10:10:10")
                .rangeEnd("2025-10-10 10:10:10")
                .from(0)
                .size(10)
                .build();
    }

    public static PublicGetEventParamsDto getPublicGetEventParamsDto() {
        return PublicGetEventParamsDto.builder()
                .text("Desc")
                .categories(List.of(1L))
                .paid(false)
                .rangeStart("2020-01-01 10:10:10")
                .rangeEnd("2027-01-01 10:10:10")
                .onlyAvailable(true)
                .sort("EVENT_DATE")
                .from(0)
                .size(10)
                .build();
    }

    public static List<ParticipationRequestDto> getParticipationRequestDtoList() {
        ParticipationRequestDto requestDto = new ParticipationRequestDto();
        requestDto.setId(1L);
        requestDto.setCreated("2024-07-01 10:10:10");
        requestDto.setEvent(1L);
        requestDto.setRequester(3L);
        requestDto.setStatus("PENDING");
        return List.of(requestDto);
    }

    public static ParticipationRequest getParticipationRequest() {
        return ParticipationRequest.builder()
                .id(1L)
                .event(new Event())
                .requester(new User())
                .created(LocalDateTime.now())
                .status(ParticipationRequestStatus.PENDING)
                .build();
    }

    public static List<StatsResponseDto> getStatsResponseDto() {
        StatsResponseDto statsResponseDto = new StatsResponseDto();
        statsResponseDto.setUri("/events/1");
        statsResponseDto.setApp("ewm-service");
        statsResponseDto.setHits(1);
        return List.of(statsResponseDto);
    }

    public static StatsRequestDto getStatsRequestDto() {
        StatsRequestDto requestDto = new StatsRequestDto(
                "ewm-main-service",
                "/events/1",
                "127.0.0.1",
                LocalDateTime.now());

        return requestDto;
    }


    public static EventRequestStatusUpdateResult getEventRequestStatusUpdateResult() {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(null);
        result.setRejectedRequests(null);
        return result;
    }
}
