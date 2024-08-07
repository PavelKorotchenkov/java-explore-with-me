package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestMapper participationRequestMapper;

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getAllByRequesterId(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id='" + userId + "' not found"));

        List<ParticipationRequest> result = participationRequestRepository.findByRequesterId(userId);
        return result.stream()
                .map(participationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto create(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id='" + userId + "' not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id='" + eventId + "' not found"));

        long participantsCount = participationRequestRepository.countByEventIdAndStatus(event.getId(), ParticipationRequestStatus.CONFIRMED);
        long participantsLimit = event.getParticipantLimit();
        validateRequest(userId, eventId, event, participantsCount, participantsLimit);

        ParticipationRequest participationRequest = new ParticipationRequest();

        if (!event.getRequestModeration() || participantsLimit == 0) {
            participationRequest.setStatus(ParticipationRequestStatus.CONFIRMED);
            eventRepository.save(event);
        } else {
            participationRequest.setStatus(ParticipationRequestStatus.PENDING);
        }

        participationRequest.setRequester(user);
        participationRequest.setCreated(LocalDateTime.now());
        participationRequest.setEvent(event);

        ParticipationRequest savedParticipationRequest = participationRequestRepository.save(participationRequest);
        return participationRequestMapper.toDto(savedParticipationRequest);
    }

    @Override
    public ParticipationRequestDto cancel(long userId, long requestId) {
        ParticipationRequest participationRequest = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("request with id='" + requestId + "' not found"));

        if (participationRequest.getRequester().getId() != userId) {
            throw new EntityNotFoundException("request with id='" + requestId + "' not found");
        }

        participationRequest.setStatus(ParticipationRequestStatus.CANCELED);
        return participationRequestMapper.toDto(participationRequestRepository.save(participationRequest));
    }

    private void validateRequest(long userId, long eventId, Event event, long countParticipants, long participantsLimit) {
        //duplicate request is forbidden (status code 409 expected)
        Optional<ParticipationRequest> existingRequest = participationRequestRepository.findByEventIdAndRequesterId(eventId, userId);
        if (existingRequest.isPresent()) {
            throw new ParticipationDuplicateRequestException("Request has already been sent");
        }

        //event initiator cannot request participation in his own event (status code 409 expected)
        if (event.getInitiator().getId() == userId) {
            throw new ParticipationRequestByInitiatorException("Request for event participation by it's initiator is forbidden");
        }

        //cannot participate in not published event (status code 409 expected)
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new PaticipationNotPublishedException("Request for unpublished event is forbidden");
        }

        //if the event has reached the limit of participation requests - (status code 409 expected)
        if (participantsLimit != 0 && countParticipants == participantsLimit) {
            throw new ParticipationLimitExceededException("Participant limit='" + event.getParticipantLimit() + "' exceeded");
        }
    }
}
