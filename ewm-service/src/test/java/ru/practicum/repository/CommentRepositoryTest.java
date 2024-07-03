package ru.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.dto.comment.CommentCountDto;
import ru.practicum.dto.comment.CommentShort;
import ru.practicum.model.*;
import ru.practicum.util.DataUtils;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        locationRepository.deleteAll();
    }

    @Autowired
    public CommentRepositoryTest(CommentRepository commentRepository,
                                 EventRepository eventRepository,
                                 UserRepository userRepository,
                                 CategoryRepository categoryRepository,
                                 LocationRepository locationRepository) {
        this.commentRepository = commentRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
    }

    @Test
    @DisplayName("Test find all comments by event id functionality")
    void givenEventId_whenFindAllByEventId_thenCommentShortsAreReturned() {
        Category category = DataUtils.getCategoryTransient();
        categoryRepository.save(category);

        User initiator = DataUtils.getUserInitiatorTransient();
        userRepository.save(initiator);
        User author = DataUtils.getUserAuthorTransient();
        userRepository.save(author);

        Location location = DataUtils.getLocationTransient();
        locationRepository.save(location);

        Event event = DataUtils.getEventTransient(category, initiator, location);
        eventRepository.save(event);

        Comment comment1 = DataUtils.getCommentOneTransient(event, author);
        Comment comment2 = DataUtils.getCommentTwoTransient(event, author);
        commentRepository.save(comment1);
        commentRepository.save(comment2);

        List<CommentShort> commentShorts = commentRepository.findAllByEventId(event.getId(), Pageable.unpaged()).getContent();
        assertThat(commentShorts).isNotNull();
        assertThat(commentShorts.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test find comment by event id and comment id functionality")
    void givenEventIdAndCommentId_whenFindByEventIdAndId_thenCommentIsReturned() {
        Category category = DataUtils.getCategoryTransient();
        categoryRepository.save(category);

        User initiator = DataUtils.getUserInitiatorTransient();
        userRepository.save(initiator);
        User author = DataUtils.getUserAuthorTransient();
        userRepository.save(author);

        Location location = DataUtils.getLocationTransient();
        locationRepository.save(location);

        Event event = DataUtils.getEventTransient(category, initiator, location);
        eventRepository.save(event);

        Comment comment1 = DataUtils.getCommentOneTransient(event, author);
        Comment comment2 = DataUtils.getCommentTwoTransient(event, author);
        commentRepository.save(comment1);
        commentRepository.save(comment2);

        CommentShort commentShort1 = commentRepository.findByEventIdAndId(event.getId(), comment1.getId());
        CommentShort commentShort2 = commentRepository.findByEventIdAndId(event.getId(), comment2.getId());
        assertThat(commentShort1).isNotNull();
        assertThat(commentShort1.getText()).isEqualTo("Comment1");
        assertThat(commentShort2).isNotNull();
        assertThat(commentShort2.getText()).isEqualTo("Comment2");
    }

    @Test
    @DisplayName("Test count comments by event id functionality")
    void givenEventId_whenCountByEventId_thenCommentCountDtosAreReturned() {
        Category category = DataUtils.getCategoryTransient();
        categoryRepository.save(category);

        User initiator = DataUtils.getUserInitiatorTransient();
        userRepository.save(initiator);
        User author = DataUtils.getUserAuthorTransient();
        userRepository.save(author);

        Location location = DataUtils.getLocationTransient();
        locationRepository.save(location);

        Event event = DataUtils.getEventTransient(category, initiator, location);
        eventRepository.save(event);

        Comment comment1 = DataUtils.getCommentOneTransient(event, author);
        Comment comment2 = DataUtils.getCommentTwoTransient(event, author);
        commentRepository.save(comment1);
        commentRepository.save(comment2);

        List<CommentCountDto> result = commentRepository.countByEventId(Set.of(event.getId()));
        assertThat(result).isNotNull();
        assertThat(result.get(0).getCommentCount()).isEqualTo(2);
    }
}