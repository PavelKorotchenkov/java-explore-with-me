package ru.practicum.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.participation.ParticipationRequestDto;
import ru.practicum.dto.participation.ParticipationRequestMapper;
import ru.practicum.enums.EventState;
import ru.practicum.enums.ParticipationRequestStatus;
import ru.practicum.exception.ParticipationDuplicateRequestException;
import ru.practicum.exception.ParticipationLimitExceededException;
import ru.practicum.exception.ParticipationRequestByInitiatorException;
import ru.practicum.exception.PaticipationNotPublishedException;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.util.DataUtils;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ParticipationServiceImplTest {

    @InjectMocks
    private ParticipationServiceImpl participationServiceTest;
    @Mock
    private ParticipationRequestRepository participationRequestRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ParticipationRequestMapper participationRequestMapper;

    @Test
    @DisplayName("Test get all by requester id functionality")
    void givenUserId_whenGetAllByRequesterId_thenListOfParticipationRequestDtosIsReturned() {
        List<ParticipationRequest> participationRequestList = List.of(DataUtils.getParticipationRequest());
        BDDMockito.given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(DataUtils.getUserRequesterPersisted()));
        BDDMockito.given(participationRequestRepository.findByRequesterId(anyLong()))
                .willReturn(participationRequestList);
        BDDMockito.given(participationRequestMapper.toDto(any(ParticipationRequest.class)))
                .willReturn(DataUtils.getParticipationRequestDtoList().get(0));

        List<ParticipationRequestDto> result = participationServiceTest.getAllByRequesterId(3L);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test get all by incorrect requester id functionality")
    void givenIncorrectUserId_whenGetAllByRequesterId_thenExceptionIsThrown() {
        long requesterId = 3L;
        BDDMockito.given(userRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> participationServiceTest.getAllByRequesterId(requesterId)
        );

        assertThat(exception.getMessage()).isEqualTo("User with id='" + requesterId + "' not found");
    }

    @Test
    @DisplayName("Test create participation request functionality")
    void givenUserIdAndEventId_whenCreate_thenParticipationRequestDtoIsReturned() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        BDDMockito.given(userRepository.findById(anyLong())).willReturn(Optional.of(DataUtils.getUserRequesterPersisted()));
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        BDDMockito.given(participationRequestRepository.countByEventIdAndStatus(anyLong(), any(ParticipationRequestStatus.class)))
                .willReturn(1L);
        BDDMockito.given(participationRequestRepository.findByEventIdAndRequesterId(anyLong(), anyLong()))
                .willReturn(Optional.empty());
        BDDMockito.given(participationRequestRepository.save(any(ParticipationRequest.class)))
                .willReturn(DataUtils.getParticipationRequest());
        BDDMockito.given(participationRequestMapper.toDto(any(ParticipationRequest.class)))
                .willReturn(DataUtils.getParticipationRequestDtoList().get(0));

        long requesterId = 3L;
        ParticipationRequestDto result = participationServiceTest.create(requesterId, 1L);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test create participation request duplicate must throw an exception functionality")
    void givenDuplicateRequest_whenCreate_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        BDDMockito.given(userRepository.findById(anyLong())).willReturn(Optional.of(DataUtils.getUserRequesterPersisted()));
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        BDDMockito.given(participationRequestRepository.countByEventIdAndStatus(anyLong(), any(ParticipationRequestStatus.class)))
                .willReturn(1L);
        BDDMockito.given(participationRequestRepository.findByEventIdAndRequesterId(anyLong(), anyLong()))
                .willReturn(Optional.of(DataUtils.getParticipationRequest()));

        long requesterId = 3L;
        ParticipationDuplicateRequestException exception = assertThrows(
                ParticipationDuplicateRequestException.class,
                () -> participationServiceTest.create(requesterId, 1L)
        );

        assertThat(exception.getMessage()).isEqualTo("Request has already been sent");
    }

    @Test
    @DisplayName("Test create participation request when requester = initiator must throw an exception functionality")
    void givenRequesterIdEqualsInitiatorId_whenCreate_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        BDDMockito.given(userRepository.findById(anyLong())).willReturn(Optional.of(DataUtils.getUserRequesterPersisted()));
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        BDDMockito.given(participationRequestRepository.countByEventIdAndStatus(anyLong(), any(ParticipationRequestStatus.class)))
                .willReturn(1L);
        BDDMockito.given(participationRequestRepository.findByEventIdAndRequesterId(anyLong(), anyLong()))
                .willReturn(Optional.empty());

        long requesterId = 1L;
        ParticipationRequestByInitiatorException exception = assertThrows(
                ParticipationRequestByInitiatorException.class,
                () -> participationServiceTest.create(requesterId, 1L)
        );

        assertThat(exception.getMessage()).isEqualTo("Request for event participation by it's initiator is forbidden");
    }

    @Test
    @DisplayName("Test create participation request when event is not published must throw an exception functionality")
    void givenEventIsPending_whenCreate_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        eventPersisted.setState(EventState.PENDING);
        BDDMockito.given(userRepository.findById(anyLong())).willReturn(Optional.of(DataUtils.getUserRequesterPersisted()));
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        BDDMockito.given(participationRequestRepository.countByEventIdAndStatus(anyLong(), any(ParticipationRequestStatus.class)))
                .willReturn(1L);
        BDDMockito.given(participationRequestRepository.findByEventIdAndRequesterId(anyLong(), anyLong()))
                .willReturn(Optional.empty());

        long requesterId = 3L;
        PaticipationNotPublishedException exception = assertThrows(
                PaticipationNotPublishedException.class,
                () -> participationServiceTest.create(requesterId, 1L)
        );

        assertThat(exception.getMessage()).isEqualTo("Request for unpublished event is forbidden");
    }

    @Test
    @DisplayName("Test create participation request when event is not published must throw an exception functionality")
    void givenEventIsCanceled_whenCreate_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        eventPersisted.setState(EventState.CANCELED);
        BDDMockito.given(userRepository.findById(anyLong())).willReturn(Optional.of(DataUtils.getUserRequesterPersisted()));
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        BDDMockito.given(participationRequestRepository.countByEventIdAndStatus(anyLong(), any(ParticipationRequestStatus.class)))
                .willReturn(1L);
        BDDMockito.given(participationRequestRepository.findByEventIdAndRequesterId(anyLong(), anyLong()))
                .willReturn(Optional.empty());

        long requesterId = 3L;
        PaticipationNotPublishedException exception = assertThrows(
                PaticipationNotPublishedException.class,
                () -> participationServiceTest.create(requesterId, 1L)
        );

        assertThat(exception.getMessage()).isEqualTo("Request for unpublished event is forbidden");
    }

    @Test
    @DisplayName("Test create participation request when event has reached the limit of participation requests must throw an exception functionality")
    void givenParticipationRequestsHasReachedTheLimit_whenCreate_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        eventPersisted.setParticipantLimit(1);
        BDDMockito.given(userRepository.findById(anyLong())).willReturn(Optional.of(DataUtils.getUserRequesterPersisted()));
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        BDDMockito.given(participationRequestRepository.countByEventIdAndStatus(anyLong(), any(ParticipationRequestStatus.class)))
                .willReturn(1L);
        BDDMockito.given(participationRequestRepository.findByEventIdAndRequesterId(anyLong(), anyLong()))
                .willReturn(Optional.empty());

        long requesterId = 3L;
        ParticipationLimitExceededException exception = assertThrows(
                ParticipationLimitExceededException.class,
                () -> participationServiceTest.create(requesterId, 1L)
        );

        assertThat(exception.getMessage()).isEqualTo("Participant limit='" + eventPersisted.getParticipantLimit() + "' exceeded");
    }

    @Test
    @DisplayName("Test create participation request with status check functionality")
    void givenEventWithoutModerationAndLimit_whenCreate_thenParticipationRequestDtoIsReturnedWithStatusConfirmed() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );

        BDDMockito.given(userRepository.findById(anyLong())).willReturn(Optional.of(DataUtils.getUserRequesterPersisted()));
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        BDDMockito.given(participationRequestRepository.countByEventIdAndStatus(anyLong(), any(ParticipationRequestStatus.class)))
                .willReturn(1L);
        BDDMockito.given(participationRequestRepository.findByEventIdAndRequesterId(anyLong(), anyLong()))
                .willReturn(Optional.empty());
        BDDMockito.given(participationRequestRepository.save(any(ParticipationRequest.class)))
                .willReturn(DataUtils.getParticipationRequest());
        BDDMockito.given(participationRequestMapper.toDto(any(ParticipationRequest.class)))
                .willReturn(DataUtils.getParticipationRequestDtoList().get(0));

        long requesterId = 3L;
        ParticipationRequestDto result = participationServiceTest.create(requesterId, 1L);

        assertThat(result).isNotNull();

        ArgumentCaptor<ParticipationRequest> captor = ArgumentCaptor.forClass(ParticipationRequest.class);
        verify(participationRequestRepository).save(captor.capture());
        ParticipationRequest capturedRequest = captor.getValue();

        assertThat(capturedRequest.getStatus()).isEqualTo(ParticipationRequestStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Test create participation request with status check functionality")
    void givenEventWithModerationAndLimit_whenCreate_thenParticipationRequestDtoIsReturnedWithStatusPending() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );

        eventPersisted.setRequestModeration(true);

        BDDMockito.given(userRepository.findById(anyLong())).willReturn(Optional.of(DataUtils.getUserRequesterPersisted()));
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        BDDMockito.given(participationRequestRepository.countByEventIdAndStatus(anyLong(), any(ParticipationRequestStatus.class)))
                .willReturn(1L);
        BDDMockito.given(participationRequestRepository.findByEventIdAndRequesterId(anyLong(), anyLong()))
                .willReturn(Optional.empty());
        BDDMockito.given(participationRequestRepository.save(any(ParticipationRequest.class)))
                .willReturn(DataUtils.getParticipationRequest());
        BDDMockito.given(participationRequestMapper.toDto(any(ParticipationRequest.class)))
                .willReturn(DataUtils.getParticipationRequestDtoList().get(0));

        long requesterId = 3L;
        ParticipationRequestDto result = participationServiceTest.create(requesterId, 1L);

        assertThat(result).isNotNull();

        ArgumentCaptor<ParticipationRequest> captor = ArgumentCaptor.forClass(ParticipationRequest.class);
        verify(participationRequestRepository).save(captor.capture());
        ParticipationRequest capturedRequest = captor.getValue();

        assertThat(capturedRequest.getStatus()).isEqualTo(ParticipationRequestStatus.PENDING);
    }


    @Test
    @DisplayName("Test cancel functionality")
    void givenUserIdAndRequestIt_whenCancel_thenParticipationRequestDtoIsReturnedAndStatusIsCanceled() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );

        BDDMockito.given(participationRequestRepository.findById(anyLong()))
                .willReturn(Optional.of(DataUtils.getParticipationRequestConfirmedPersisted(
                        eventPersisted,
                        DataUtils.getUserRequesterPersisted()
                )));

        BDDMockito.given(participationRequestRepository.save(any(ParticipationRequest.class)))
                .willReturn(DataUtils.getParticipationRequest());

        ParticipationRequestDto participationRequestDto = DataUtils.getParticipationRequestDtoList().get(0);
        BDDMockito.given(participationRequestMapper.toDto(any(ParticipationRequest.class)))
                .willReturn(participationRequestDto);

        ParticipationRequestDto result = participationServiceTest.cancel(3L, 1L);

        assertThat(result).isNotNull();

        ArgumentCaptor<ParticipationRequest> captor = ArgumentCaptor.forClass(ParticipationRequest.class);
        verify(participationRequestRepository).save(captor.capture());
        ParticipationRequest capturedRequest = captor.getValue();
        assertThat(capturedRequest.getStatus()).isEqualTo(ParticipationRequestStatus.CANCELED);
    }

    @Test
    @DisplayName("Test cancel with incorrect user id functionality")
    void givenIncorrectUserId_whenCancel_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );

        BDDMockito.given(participationRequestRepository.findById(anyLong()))
                .willReturn(Optional.of(DataUtils.getParticipationRequestConfirmedPersisted(
                        eventPersisted,
                        DataUtils.getUserRequesterPersisted()
                )));

        long requesterId = 1L;
        long requestId = 1L;

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> participationServiceTest.cancel(requesterId, requestId)
        );

        assertThat(exception.getMessage()).isEqualTo("request with id='" + requestId + "' not found");
    }
}