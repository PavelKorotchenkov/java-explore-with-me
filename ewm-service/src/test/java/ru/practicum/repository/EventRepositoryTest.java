package ru.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;
import ru.practicum.util.DataUtils;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class EventRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        locationRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("Test find events by ids functionality")
    void givenTwoEventsId_whenFindByIdIn_thenSetOfTwoEventsAreReturned() {
        Category category = DataUtils.getCategoryTransient();
        categoryRepository.save(category);
        User initiator = DataUtils.getUserInitiatorTransient();
        userRepository.save(initiator);
        Location location = DataUtils.getLocationTransient();
        locationRepository.save(location);
        Event event1 = DataUtils.getEventTransient(category, initiator, location);
        eventRepository.save(event1);
        Event event2 = DataUtils.getEventTransient(category, initiator, location);
        eventRepository.save(event2);

        Set<Event> eventSet = eventRepository.findByIdIn(Set.of(event1.getId(), event2.getId()));

        assertThat(eventSet).isNotNull();
        assertThat(eventSet.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test find event by initiator id functionality")
    void givenInitiatorIdAndTwoEvents_whenFindByInitiatorId_thenTwoEventsAreReturned() {
        Category category = DataUtils.getCategoryTransient();
        categoryRepository.save(category);
        User initiator = DataUtils.getUserInitiatorTransient();
        userRepository.save(initiator);
        Location location = DataUtils.getLocationTransient();
        locationRepository.save(location);
        Event event1 = DataUtils.getEventTransient(category, initiator, location);
        eventRepository.save(event1);
        Event event2 = DataUtils.getEventTransient(category, initiator, location);
        eventRepository.save(event2);

        Page<Event> eventPage = eventRepository.findByInitiatorId(initiator.getId(), Pageable.unpaged());

        assertThat(eventPage).isNotNull();
        assertThat(eventPage.getContent().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test find event by category id functionality")
    void givenCategoryId_whenFindFirstByCategoryId_thenOneEventIsReturned() {
        Category category = DataUtils.getCategoryTransient();
        categoryRepository.save(category);
        User initiator = DataUtils.getUserInitiatorTransient();
        userRepository.save(initiator);
        Location location = DataUtils.getLocationTransient();
        locationRepository.save(location);
        Event event1 = DataUtils.getEventTransient(category, initiator, location);
        eventRepository.save(event1);
        Event event2 = DataUtils.getEventTransient(category, initiator, location);
        eventRepository.save(event2);

        Optional<Event> foundEvent = eventRepository.findFirstByCategoryId(category.getId());

        assertTrue(foundEvent.isPresent());
    }
}