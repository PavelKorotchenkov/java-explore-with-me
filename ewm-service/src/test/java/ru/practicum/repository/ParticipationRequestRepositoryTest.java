package ru.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.dto.participation.ParticipantCountDto;
import ru.practicum.enums.ParticipationRequestStatus;
import ru.practicum.model.*;
import ru.practicum.util.DataUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ParticipationRequestRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ParticipationRequestRepository participationRequestRepository;

    @BeforeEach
    void clear() {
        eventRepository.deleteAll();
        locationRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        participationRequestRepository.deleteAll();
    }

    @Test
    @DisplayName("Test find participation requests by requester id functionality")
    void givenRequesterIdAndTwoRequests_whenFindByRequesterId_thenTwoRequestsAreReturned() {
        Category category = DataUtils.getCategoryTransient();
        categoryRepository.save(category);
        User initiator = DataUtils.getUserInitiatorTransient();
        userRepository.save(initiator);
        User requester = DataUtils.getUserRequesterTransient();
        userRepository.save(requester);
        Location location = DataUtils.getLocationTransient();
        locationRepository.save(location);
        Event event1 = DataUtils.getEventTransient(category, initiator, location);
        eventRepository.save(event1);
        Event event2 = DataUtils.getEventTransient(category, initiator, location);
        eventRepository.save(event2);

        ParticipationRequest request1 = DataUtils.getParticipationRequestConfirmedTransient(event1, requester);
        ParticipationRequest request2 = DataUtils.getParticipationRequestConfirmedTransient(event2, requester);
        participationRequestRepository.save(request1);
        participationRequestRepository.save(request2);

        List<ParticipationRequest> result = participationRequestRepository.findByRequesterId(requester.getId());

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test find participation requests by event id excluding initiator id functionality")
    void givenEventIdAndUserIdAndTwoRequesters_whenFindByEventIdAndRequesterIdNot_thenTwoRequestsAreReturned() {
        Category category = DataUtils.getCategoryTransient();
        categoryRepository.save(category);
        User initiator = DataUtils.getUserInitiatorTransient();
        userRepository.save(initiator);
        User requester1 = DataUtils.getUserRequesterTransient();
        userRepository.save(requester1);
        User requester2 = DataUtils.getUserRequesterTransient();
        requester2.setEmail("requester2@mail.com");
        userRepository.save(requester2);
        Location location = DataUtils.getLocationTransient();
        locationRepository.save(location);
        Event event = DataUtils.getEventTransient(category, initiator, location);
        eventRepository.save(event);

        ParticipationRequest request1 = DataUtils.getParticipationRequestConfirmedTransient(event, requester1);
        ParticipationRequest request2 = DataUtils.getParticipationRequestConfirmedTransient(event, requester2);
        participationRequestRepository.save(request1);
        participationRequestRepository.save(request2);

        List<ParticipationRequest> result = participationRequestRepository
                .findByEventIdAndRequesterIdNot(event.getId(), initiator.getId());

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test find participation request by event id and initiator id functionality")
    void givenEventIdAndRequesterId_whenFindByEventIdAndRequesterId_thenOneRequestIsReturned() {
        Category category = DataUtils.getCategoryTransient();
        categoryRepository.save(category);
        User initiator = DataUtils.getUserInitiatorTransient();
        userRepository.save(initiator);
        User requester = DataUtils.getUserRequesterTransient();
        userRepository.save(requester);
        Location location = DataUtils.getLocationTransient();
        locationRepository.save(location);
        Event event = DataUtils.getEventTransient(category, initiator, location);
        eventRepository.save(event);

        ParticipationRequest request = DataUtils.getParticipationRequestConfirmedTransient(event, requester);
        participationRequestRepository.save(request);

        Optional<ParticipationRequest> result = participationRequestRepository
                .findByEventIdAndRequesterId(event.getId(), requester.getId());

        assertTrue(result.isPresent());
        assertThat(result.get().getRequester().getId()).isEqualTo(requester.getId());
    }

    @Test
    @DisplayName("Test count participants of by events id and request status functionality ")
    void givenTwoEventIdsAndConfirmedStatus_whenCountParticipantsInAndStatus_thenTwoParticipantCountDtosWithOneConfirmedRequestAreReturned() {
        Category category = DataUtils.getCategoryTransient();
        categoryRepository.save(category);
        User initiator = DataUtils.getUserInitiatorTransient();
        userRepository.save(initiator);
        User requester = DataUtils.getUserRequesterTransient();
        userRepository.save(requester);
        Location location = DataUtils.getLocationTransient();
        locationRepository.save(location);
        Event event1 = DataUtils.getEventTransient(category, initiator, location);
        eventRepository.save(event1);
        Event event2 = DataUtils.getEventTransient(category, initiator, location);
        eventRepository.save(event2);

        ParticipationRequest request1 = DataUtils.getParticipationRequestConfirmedTransient(event1, requester);
        ParticipationRequest request2 = DataUtils.getParticipationRequestConfirmedTransient(event2, requester);
        participationRequestRepository.save(request1);
        participationRequestRepository.save(request2);

        List<ParticipantCountDto> result = participationRequestRepository.countParticipantsInAndStatus(Set.of(event1.getId(), event2.getId()), ParticipationRequestStatus.CONFIRMED);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getConfirmedRequestCount()).isEqualTo(1);
        assertThat(result.get(1).getConfirmedRequestCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Test count participation requests by event id and status functionality")
    void givenOneEventIdAndStatusConfirmedAndTwoRequests_whenCountByEventIdAndStatus_thenTwoRequestsAreReturned() {
        Category category = DataUtils.getCategoryTransient();
        categoryRepository.save(category);
        User initiator = DataUtils.getUserInitiatorTransient();
        userRepository.save(initiator);
        User requester1 = DataUtils.getUserRequesterTransient();
        userRepository.save(requester1);
        User requester2 = DataUtils.getUserRequesterTransient();
        requester2.setEmail("requester2@mail.com");
        userRepository.save(requester2);
        Location location = DataUtils.getLocationTransient();
        locationRepository.save(location);
        Event event = DataUtils.getEventTransient(category, initiator, location);
        eventRepository.save(event);

        ParticipationRequest request1 = DataUtils.getParticipationRequestConfirmedTransient(event, requester1);
        ParticipationRequest request2 = DataUtils.getParticipationRequestConfirmedTransient(event, requester2);
        participationRequestRepository.save(request1);
        participationRequestRepository.save(request2);

        Long confirmed = participationRequestRepository.countByEventIdAndStatus(event.getId(), ParticipationRequestStatus.CONFIRMED);
        Long pending = participationRequestRepository.countByEventIdAndStatus(event.getId(), ParticipationRequestStatus.PENDING);

        assertThat(confirmed).isNotNull();
        assertThat(confirmed).isEqualTo(2);

        assertThat(pending).isEqualTo(0);
    }
}