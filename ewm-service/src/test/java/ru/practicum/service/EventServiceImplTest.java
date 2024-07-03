package ru.practicum.service;

import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.client.Client;
import ru.practicum.dto.ClientRequestDto;
import ru.practicum.dto.event.*;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.dto.location.LocationDtoMapper;
import ru.practicum.dto.participation.*;
import ru.practicum.enums.EventState;
import ru.practicum.enums.ParticipationRequestStatus;
import ru.practicum.exception.EventUpdateException;
import ru.practicum.model.*;
import ru.practicum.repository.*;
import ru.practicum.util.DataUtils;
import ru.practicum.util.LocalDateTimeStringParser;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @InjectMocks
    private EventServiceImpl eventServiceTest;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private ParticipationRequestRepository participationRequestRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ParticipationRequestMapper participationRequestMapper;
    @Mock
    private LocationDtoMapper locationDtoMapper;
    @Mock
    private EventDtoMapper eventDtoMapper;
    @Mock
    private Client client;


    @Test
    @DisplayName("Test get all by admin functionality")
    void givenParams_whenGetAll_thenListOfEventsAreReturned() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );

        Page<Event> pageEvents = new PageImpl<>(List.of(eventPersisted), Pageable.unpaged(), 1);
        BDDMockito.given(eventRepository.findAll(any(Predicate.class), any(Pageable.class))).willReturn(pageEvents);
        BDDMockito.given(participationRequestRepository.countParticipantsInAndStatus(anySet(), any(ParticipationRequestStatus.class))).willReturn(new ArrayList<ParticipantCountDto>());
        BDDMockito.given(eventDtoMapper.eventToEventFullDto(any(Event.class))).willReturn(new EventFullDto());

        List<EventFullDto> result = eventServiceTest.getAll(DataUtils.getAdminEventParamsDto());

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test update event by admin functionality")
    void givenEventIdAndEventToUpdate_whenUpdateByAdmin_thenEventFullDtoIsReturned() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );

        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        BDDMockito.given(eventDtoMapper.eventToEventFullDto(any(Event.class))).willReturn(DataUtils.getEventFullDto());
        BDDMockito.given(eventRepository.save(any(Event.class))).willReturn(eventPersisted);
        BDDMockito.given(categoryRepository.findById(anyLong())).willReturn(Optional.of(new Category()));
        BDDMockito.given(locationDtoMapper.locationDtoToLocation(any(LocationDto.class))).willReturn(DataUtils.getLocationTransient());
        BDDMockito.given(locationRepository.save(any(Location.class))).willReturn(DataUtils.getLocationPersisted());
        EventFullDto eventFullDto = eventServiceTest.updateByAdmin(1L, DataUtils.getUpdateEventAdminRequest());

        assertThat(eventFullDto).isNotNull();
    }

    @Test
    @DisplayName("Test update event by admin with incorrect event id functionality")
    void givenIncorrectEventId_whenUpdateByAdmin_thenExceptionIsThrown() {
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> eventServiceTest.updateByAdmin(1L, new UpdateEventAdminRequest())
        );

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("Test update event by admin with incorrect event date time in the past functionality")
    void givenIncorrectEventDateInThePast_whenUpdateByAdmin_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        LocalDateTime incorrectEventDate = LocalDateTime.now().minusHours(1);
        String incorrectEventDateString = LocalDateTimeStringParser.parseLocalDateTimeToString(incorrectEventDate);
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        UpdateEventAdminRequest eventToUpdate = new UpdateEventAdminRequest();
        eventToUpdate.setEventDate(incorrectEventDateString);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> eventServiceTest.updateByAdmin(1L, eventToUpdate)
        );

        assertThat(exception.getMessage()).isEqualTo("The event date can't be in the past");

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("Test update event by admin with incorrect event date time 1 hour before publication functionality")
    void givenIncorrectEventDate1HourBeforePublication_whenUpdateByAdmin_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        LocalDateTime incorrectEventDate = LocalDateTime.now().plusMinutes(59);
        String incorrectEventDateString = LocalDateTimeStringParser.parseLocalDateTimeToString(incorrectEventDate);
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        UpdateEventAdminRequest eventToUpdate = new UpdateEventAdminRequest();
        eventToUpdate.setEventDate(incorrectEventDateString);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> eventServiceTest.updateByAdmin(1L, eventToUpdate)
        );

        assertThat(exception.getMessage()).isEqualTo("The event date must be at least one hour before the publication date");

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("Test update event by admin with incorrect state action functionality")
    void givenIncorrectEventStateAction_whenUpdateByAdmin_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        UpdateEventAdminRequest eventToUpdate = new UpdateEventAdminRequest();
        eventToUpdate.setStateAction("Incorrect state action");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> eventServiceTest.updateByAdmin(1L, eventToUpdate)
        );

        assertThat(exception.getMessage()).isEqualTo("StateAction='" + eventToUpdate.getStateAction() + "' is not valid");

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("Test update event by admin when event is published and state action is PUBLISH_EVENT functionality")
    void givenEventIsAlreadyPublishedAndTryToPublishAgain_whenUpdateByAdmin_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        UpdateEventAdminRequest eventToUpdate = new UpdateEventAdminRequest();
        eventToUpdate.setStateAction("PUBLISH_EVENT");
        EventUpdateException exception = assertThrows(
                EventUpdateException.class,
                () -> eventServiceTest.updateByAdmin(1L, eventToUpdate)
        );

        assertThat(exception.getMessage()).isEqualTo("Attempt to publish or reject the event failed because it is already published");

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("Test update event by admin when event is published and state action is REJECT_EVENT functionality")
    void givenEventIsAlreadyPublishedAndTryToReject_whenUpdateByAdmin_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        UpdateEventAdminRequest eventToUpdate = new UpdateEventAdminRequest();
        eventToUpdate.setStateAction("REJECT_EVENT");
        EventUpdateException exception = assertThrows(
                EventUpdateException.class,
                () -> eventServiceTest.updateByAdmin(1L, eventToUpdate)
        );

        assertThat(exception.getMessage()).isEqualTo("Attempt to publish or reject the event failed because it is already published");

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("Test update event by admin when event is canceled and state action is PUBLISH_EVENT functionality")
    void givenEventIsCanceledAndTryToPublishEvent_whenUpdateByAdmin_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        eventPersisted.setState(EventState.CANCELED);
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        UpdateEventAdminRequest eventToUpdate = new UpdateEventAdminRequest();
        eventToUpdate.setStateAction("PUBLISH_EVENT");
        EventUpdateException exception = assertThrows(
                EventUpdateException.class,
                () -> eventServiceTest.updateByAdmin(1L, eventToUpdate)
        );

        assertThat(exception.getMessage()).isEqualTo("Attempt to publish the event failed because it can be published only if it's in pending state");

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("Test update event by admin with incorrect category id functionality")
    void givenIncorrectCatId_whenUpdateByAdmin_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );

        UpdateEventAdminRequest eventToUpdate = new UpdateEventAdminRequest();
        eventToUpdate.setCategory(0L);

        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        BDDMockito.given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(
                EventUpdateException.class,
                () -> eventServiceTest.updateByAdmin(1L, eventToUpdate)
        );

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("Test get all by initiator functionality")
    void givenInitiatorId_whenGetAllByInitiator_thenListOfEventShortDtosIsReturned() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        Page<Event> pageEvent = new PageImpl<>(List.of(eventPersisted), Pageable.unpaged(), 1);

        EventShortDto eventShortDto = DataUtils.getEventShortDto();
        BDDMockito.given(eventRepository.findByInitiatorId(anyLong(), any(Pageable.class))).willReturn(pageEvent);
        BDDMockito.given(eventDtoMapper.eventToEventShortDto(any(Event.class))).willReturn(eventShortDto);

        List<EventShortDto> result = eventServiceTest.getAllByInitiator(1L, Pageable.unpaged());

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test create functionality")
    void givenUserIdAndNewEventDto_whenCreate_thenEventFullDtoIsReturned() {
        User initiatorPersisted = DataUtils.getUserInitiatorPersisted();
        Category categoryPersisted = DataUtils.getCategoryPersisted();
        Location locationTransient = DataUtils.getLocationPersisted();
        Event eventPersisted = DataUtils.getEventPersisted(
                categoryPersisted,
                initiatorPersisted,
                locationTransient
        );

        BDDMockito.given(eventDtoMapper.newEventDtoToEvent(any(NewEventDto.class))).willReturn(eventPersisted);
        BDDMockito.given(userRepository.findById(anyLong())).willReturn(Optional.of(initiatorPersisted));
        BDDMockito.given(categoryRepository.findById(anyLong())).willReturn(Optional.of(categoryPersisted));
        BDDMockito.given(locationDtoMapper.locationDtoToLocation(any(LocationDto.class))).willReturn(DataUtils.getLocationTransient());
        BDDMockito.given(locationRepository.save(any(Location.class))).willReturn(DataUtils.getLocationPersisted());
        BDDMockito.given(eventRepository.save(any(Event.class))).willReturn(eventPersisted);
        BDDMockito.given(eventDtoMapper.eventToEventFullDto(any(Event.class))).willReturn(DataUtils.getEventFullDto());

        EventFullDto result = eventServiceTest.create(1L, DataUtils.getNewEventDto());

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test get event full dto by initiator functionality")
    void givenUserIdAndEventId_whenGetOneByInitiator_thenEventFullDtoIsReturned() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );

        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        BDDMockito.given(eventDtoMapper.eventToEventFullDto(any(Event.class))).willReturn(DataUtils.getEventFullDto());

        EventFullDto result = eventServiceTest.getOneByInitiator(1L, 1L);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test get event full dto by initiator with incorrect initiator id functionality")
    void givenIncorrectUserIdAndEventId_whenGetOneByInitiator_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserAuthorPersisted(),
                DataUtils.getLocationPersisted()
        );

        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> eventServiceTest.getOneByInitiator(1L, 1L)
        );

        assertThat(exception.getMessage()).isEqualTo("Event with id='" + eventPersisted.getId() + "' not found");

        verify(eventDtoMapper, never()).eventToEventFullDto(any(Event.class));
    }

    @Test
    @DisplayName("Test update event by initiator with state CANCELED functionality")
    void givenUserIdAndEventIdAndUpdateRequestAndStateIsCanceled_whenUpdateByInitiator_thenEventFullDtoIsReturned() {
        Category categoryPersisted = DataUtils.getCategoryPersisted();
        Event eventPersisted = DataUtils.getEventPersisted(
                categoryPersisted,
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        eventPersisted.setState(EventState.CANCELED);
        UpdateEventUserRequest request = DataUtils.getUpdateEventUserRequest();

        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        BDDMockito.given(categoryRepository.findById(anyLong())).willReturn(Optional.of(categoryPersisted));
        BDDMockito.given(locationDtoMapper.locationDtoToLocation(any(LocationDto.class))).willReturn(DataUtils.getLocationTransient());
        BDDMockito.given(eventRepository.save(any(Event.class))).willReturn(eventPersisted);
        BDDMockito.given(eventDtoMapper.eventToEventFullDto(any(Event.class))).willReturn(DataUtils.getEventFullDto());

        EventFullDto result = eventServiceTest.updateByInitiator(1L, 1L, request);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test update event by initiator with state PENDING functionality")
    void givenUserIdAndEventIdAndUpdateRequestAndStateIsPending_whenUpdateByInitiator_thenEventFullDtoIsReturned() {
        Category categoryPersisted = DataUtils.getCategoryPersisted();
        Event eventPersisted = DataUtils.getEventPersisted(
                categoryPersisted,
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        eventPersisted.setState(EventState.PENDING);
        UpdateEventUserRequest request = DataUtils.getUpdateEventUserRequest();

        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        BDDMockito.given(categoryRepository.findById(anyLong())).willReturn(Optional.of(categoryPersisted));
        BDDMockito.given(locationDtoMapper.locationDtoToLocation(any(LocationDto.class))).willReturn(DataUtils.getLocationTransient());
        BDDMockito.given(eventRepository.save(any(Event.class))).willReturn(eventPersisted);
        BDDMockito.given(eventDtoMapper.eventToEventFullDto(any(Event.class))).willReturn(DataUtils.getEventFullDto());

        EventFullDto result = eventServiceTest.updateByInitiator(1L, 1L, request);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test update event by initiator with state is not CANCELED or PENDING must throw exception functionality")
    void givenEventIsNotCanceledOrPending_whenUpdateByInitiator_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        eventPersisted.setState(EventState.PUBLISHED);
        UpdateEventUserRequest request = DataUtils.getUpdateEventUserRequest();

        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));

        EventUpdateException exception = assertThrows(
                EventUpdateException.class,
                () -> eventServiceTest.updateByInitiator(1L, 1L, request)
        );

        assertThat(exception.getMessage()).isEqualTo("Only pending or canceled events can be changed");
    }

    @Test
    @DisplayName("Test get requests functionality")
    void givenUserIdAndEventId_whenGetRequests_thenListOfParticipationRequestsIsReturned() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        User requester = DataUtils.getUserRequesterPersisted();

        ParticipationRequest request = DataUtils.getParticipationRequestConfirmedPersisted(eventPersisted, requester);
        List<ParticipationRequest> expectedResult = List.of(request);
        BDDMockito.given(participationRequestRepository.findByEventIdAndRequesterIdNot(1L, 1L)).willReturn(expectedResult);
        BDDMockito.given(participationRequestMapper.toDto(any(ParticipationRequest.class))).willReturn(new ParticipationRequestDto());

        List<ParticipationRequestDto> result = eventServiceTest.getRequests(1L, 1L);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test update requests status confirmed functionality")
    void givenUserIdAndEventIdAndStatusConfirmed_whenUpdateRequestStatus_thenEventRequestStatusUpdateResultIsReturned() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );

        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();
        request.setRequestIds(List.of(1L));
        request.setStatus("CONFIRMED");

        List<ParticipationRequest> participationRequests = new ArrayList<>();
        User userRequesterPersisted = DataUtils.getUserRequesterPersisted();
        ParticipationRequest pendingRequest = DataUtils.getParticipationRequestConfirmedPersisted(eventPersisted, userRequesterPersisted);
        pendingRequest.setStatus(ParticipationRequestStatus.PENDING);
        participationRequests.add(pendingRequest);

        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        BDDMockito.given(participationRequestRepository.findAllById(request.getRequestIds())).willReturn(participationRequests);
        BDDMockito.given(participationRequestRepository.saveAll(anyList())).willReturn(new ArrayList<>());

        EventRequestStatusUpdateResult result = eventServiceTest.updateRequestStatus(1L, 1L, request);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test update requests status rejected functionality")
    void givenUserIdAndEventIdAndStatusRejected_whenUpdateRequestStatus_thenEventRequestStatusUpdateResultIsReturned() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );

        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();
        request.setRequestIds(List.of(1L));
        request.setStatus("REJECTED");

        List<ParticipationRequest> participationRequests = new ArrayList<>();
        User userRequesterPersisted = DataUtils.getUserRequesterPersisted();
        ParticipationRequest pendingRequest = DataUtils.getParticipationRequestConfirmedPersisted(eventPersisted, userRequesterPersisted);
        pendingRequest.setStatus(ParticipationRequestStatus.PENDING);
        participationRequests.add(pendingRequest);

        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        BDDMockito.given(participationRequestRepository.findAllById(request.getRequestIds())).willReturn(participationRequests);
        BDDMockito.given(participationRequestRepository.saveAll(anyList())).willReturn(new ArrayList<>());

        EventRequestStatusUpdateResult result = eventServiceTest.updateRequestStatus(1L, 1L, request);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test update requests with status confirmed and with participants limit and requested moderation functionality")
    void givenUserIdAndEventIdAndStatusConfirmedAndParticipantLimitAndRequestModeration_whenUpdateRequestStatus_thenEventRequestStatusUpdateResultIsReturned() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );

        eventPersisted.setParticipantLimit(2);
        eventPersisted.setRequestModeration(true);

        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();
        request.setRequestIds(List.of(1L));
        request.setStatus("CONFIRMED");

        List<ParticipationRequest> participationRequests = new ArrayList<>();
        User userRequesterPersisted = DataUtils.getUserRequesterPersisted();
        ParticipationRequest pendingRequest = DataUtils.getParticipationRequestConfirmedPersisted(eventPersisted, userRequesterPersisted);
        pendingRequest.setStatus(ParticipationRequestStatus.PENDING);
        participationRequests.add(pendingRequest);

        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        BDDMockito.given(participationRequestRepository.findAllById(request.getRequestIds())).willReturn(participationRequests);
        BDDMockito.given(participationRequestRepository
                .countByEventIdAndStatus(1L, ParticipationRequestStatus.CONFIRMED)).willReturn(1L);
        BDDMockito.given(participationRequestRepository.saveAll(anyList())).willReturn(new ArrayList<>());

        EventRequestStatusUpdateResult result = eventServiceTest.updateRequestStatus(1L, 1L, request);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test get all public functionality")
    void givenPublicEventParams_whenGetAll_thenListOfEventShortDtosIsReturned() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );

        Page<Event> pageEvents = new PageImpl<>(List.of(eventPersisted), Pageable.unpaged(), 1);
        BDDMockito.given(eventRepository.findAll(any(Predicate.class), any(Pageable.class))).willReturn(pageEvents);
        BDDMockito.given(commentRepository.countByEventId(anySet())).willReturn(Collections.EMPTY_LIST);
        BDDMockito.given(eventDtoMapper.eventToEventShortDto(any(Event.class))).willReturn(new EventShortDto());

        List<EventShortDto> result = eventServiceTest.getAll(DataUtils.getPublicGetEventParamsDto());

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test get by id functionality")
    void givenEventId_whenGetByIdPublic_thenEventFullDtoIsReturned() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        BDDMockito.given(eventDtoMapper.eventToEventFullDto(any(Event.class))).willReturn(new EventFullDto());
        BDDMockito.given(client.getStats(any(ClientRequestDto.class))).willReturn(DataUtils.getStatsResponseDto());

        EventFullDto result = eventServiceTest.getByIdPublic(1L);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test get by id with event state is not published functionality")
    void givenEventIdWithStateNotPublished_whenGetByIdPublic_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        eventPersisted.setState(EventState.PENDING);
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> eventServiceTest.getByIdPublic(1L)
        );

        assertThat(exception.getMessage()).isEqualTo("Event with id=" + eventPersisted.getId() + " not found");
    }
}