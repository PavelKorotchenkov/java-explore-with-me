package ru.practicum.service;

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
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentDtoMapper;
import ru.practicum.dto.comment.CommentShort;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.exception.CommentUpdateNotByAuthorException;
import ru.practicum.exception.CommentUpdateTimeLimitExceededException;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.util.DataUtils;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentDtoMapper commentDtoMapper;
    @InjectMocks
    private CommentServiceImpl commentServiceTest;

    @Test
    @DisplayName("Test create comment functionality")
    void givenCommentAndUserIdAndEventId_whenCreate_thenCommentIsReturned() {
        NewCommentDto newCommentDto = DataUtils.getNewCommentDto();

        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );

        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        User userAuthorPersisted = DataUtils.getUserAuthorPersisted();
        BDDMockito.given(userRepository.findById(anyLong())).willReturn(Optional.of(userAuthorPersisted));
        Comment commentOneTransient = DataUtils.getCommentOneTransient(eventPersisted, userAuthorPersisted);
        BDDMockito.given(commentDtoMapper.newCommentDtoToComment(newCommentDto))
                .willReturn(commentOneTransient);
        Comment commentOnePersisted = DataUtils.getCommentOnePersisted(eventPersisted, userAuthorPersisted);
        BDDMockito.given(commentRepository.save(commentOneTransient)).willReturn(commentOnePersisted);
        BDDMockito.given(commentDtoMapper.commentToCommentDto(commentOnePersisted)).willReturn(DataUtils.getCommentDtoOne());

        CommentDto savedComment = commentServiceTest.create(newCommentDto, 1L, 1L);

        assertThat(savedComment).isNotNull();
    }

    @Test
    @DisplayName("Test create comment with incorrect event id functionality")
    void givenCommentAndUserIdAndIncorrectEventId_whenCreate_thenExceptionIsThrown() {
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> commentServiceTest.create(DataUtils.getNewCommentDto(), 1L, 1L)
        );

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Test create comment with incorrect user id functionality")
    void givenCommentAndIncorrectUserIdAndEventId_whenCreate_thenExceptionIsThrown() {
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(new Event()));
        BDDMockito.given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> commentServiceTest.create(DataUtils.getNewCommentDto(), 1L, 1L)
        );

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Test update comment by author functionality")
    void givenNewCommentDtoAndUserIdAndEventIdAndCommentId_whenUpdateByAuthor_thenCommentDtoIsReturned() {
        NewCommentDto newCommentDto = DataUtils.getNewCommentDto();

        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));

        User userAuthorPersisted = DataUtils.getUserAuthorPersisted();
        BDDMockito.given(userRepository.findById(anyLong())).willReturn(Optional.of(userAuthorPersisted));

        Comment commentOnePersisted = DataUtils.getCommentOnePersisted(eventPersisted, userAuthorPersisted);
        BDDMockito.given(commentRepository.findById(anyLong())).willReturn(Optional.of(commentOnePersisted));

        Comment updatedCommentOnePersisted = DataUtils.getUpdatedCommentOnePersisted(eventPersisted, userAuthorPersisted);
        BDDMockito.given(commentRepository.save(any(Comment.class))).willReturn(updatedCommentOnePersisted);

        BDDMockito.given(commentDtoMapper.commentToCommentDto(any(Comment.class))).willReturn(DataUtils.getUpdatedCommentDtoOne());

        CommentDto updatedComment = commentServiceTest.updateByAuthor(newCommentDto, 2L, 1L, 1L);

        assertThat(updatedComment).isNotNull();
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("Test update comment by author with incorrect event id functionality")
    void givenNewCommentDtoAndUserIdAndIncorrectEventIdAndCommentId_whenUpdateByAuthor_thenExceptionIsThrown() {
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> commentServiceTest.updateByAuthor(new NewCommentDto(), 1L, 1L, 1l)
        );

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Test update comment by author with incorrect user id functionality")
    void givenNewCommentDtoAndIncorrectUserIdAndEventIdAndCommentId_whenUpdateByAuthor_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        BDDMockito.given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> commentServiceTest.updateByAuthor(new NewCommentDto(), 1L, 1L, 1l)
        );

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Test update comment by author with incorrect comment id functionality")
    void givenNewCommentDtoAndUserIdAndEventIdAndIncorrectCommentId_whenUpdateByAuthor_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        User userAuthorPersisted = DataUtils.getUserAuthorPersisted();
        BDDMockito.given(userRepository.findById(anyLong())).willReturn(Optional.of(userAuthorPersisted));
        BDDMockito.given(commentRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> commentServiceTest.updateByAuthor(new NewCommentDto(), 1L, 1L, 1l)
        );

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Test update comment by author with incorrect user id not being equal author id functionality")
    void givenUserId1IsNotEqualAuthorId2_whenUpdateByAuthor_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        User userAuthorPersisted = DataUtils.getUserAuthorPersisted();
        BDDMockito.given(userRepository.findById(anyLong())).willReturn(Optional.of(userAuthorPersisted));
        Comment commentOnePersisted = DataUtils.getCommentOnePersisted(eventPersisted, userAuthorPersisted);
        BDDMockito.given(commentRepository.findById(anyLong())).willReturn(Optional.of(commentOnePersisted));
        assertThrows(
                CommentUpdateNotByAuthorException.class,
                () -> commentServiceTest.updateByAuthor(new NewCommentDto(), 1L, 1L, 1L)
        );

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Test update comment by author one hour after it was created functionality")
    void givenUpdateAfter1HourAfterCommentWasCreated_whenUpdateByAuthor_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        User userAuthorPersisted = DataUtils.getUserAuthorPersisted();
        BDDMockito.given(userRepository.findById(anyLong())).willReturn(Optional.of(userAuthorPersisted));
        Comment commentOnePersisted = DataUtils.getCommentOnePersisted(eventPersisted, userAuthorPersisted);
        commentOnePersisted.setCreatedOn(LocalDateTime.now().minusMinutes(61));
        BDDMockito.given(commentRepository.findById(anyLong())).willReturn(Optional.of(commentOnePersisted));

        assertThrows(
                CommentUpdateTimeLimitExceededException.class,
                () -> commentServiceTest.updateByAuthor(new NewCommentDto(), 2L, 1L, 1L)
        );

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Test delete by author functionality")
    void givenUserIdAndEventIdAndCommentId_whenDeleteByAuthor_thenRepositoryDeleteByIdMethodUsed() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        User userAuthorPersisted = DataUtils.getUserAuthorPersisted();
        BDDMockito.given(userRepository.findById(anyLong())).willReturn(Optional.of(userAuthorPersisted));
        Comment commentOnePersisted = DataUtils.getCommentOnePersisted(eventPersisted, userAuthorPersisted);
        BDDMockito.given(commentRepository.findById(anyLong())).willReturn(Optional.of(commentOnePersisted));

        commentServiceTest.deleteByAuthor(2L, 1L, 1L);

        verify(commentRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Test delete by author with incorrect user id functionality")
    void givenIncorrectUserIdAndEventIdAndCommentId_whenDeleteByAuthor_thenExceptionIsThrown() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        User userAuthorPersisted = DataUtils.getUserAuthorPersisted();
        BDDMockito.given(userRepository.findById(anyLong())).willReturn(Optional.of(userAuthorPersisted));
        Comment commentOnePersisted = DataUtils.getCommentOnePersisted(eventPersisted, userAuthorPersisted);
        BDDMockito.given(commentRepository.findById(anyLong())).willReturn(Optional.of(commentOnePersisted));

        assertThrows(
                CommentUpdateNotByAuthorException.class,
                () -> commentServiceTest.deleteByAuthor(1, 1, 1)
        );

        verify(commentRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Test delete by admin functionality")
    void givenEventIdAndCommentId_whenDeleteByAdmin_thenRepositoryDeleteByIdMethodUsed() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));
        User userAuthorPersisted = DataUtils.getUserAuthorPersisted();
        Comment commentOnePersisted = DataUtils.getCommentOnePersisted(eventPersisted, userAuthorPersisted);
        BDDMockito.given(commentRepository.findById(anyLong())).willReturn(Optional.of(commentOnePersisted));

        commentServiceTest.deleteByAdmin(eventPersisted.getId(), commentOnePersisted.getId());

        verify(commentRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Test get all functionality")
    void givenEventId_whenGetAll_thenListOfCommentShortIsReturned() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));

        CommentShort comment1 = new CommentShort() {
            public long getId() {
                return 1L;
            }

            public String getText() {
                return "Comment 1";
            }

            public String getAuthorName() {
                return "Author 1";
            }

            public LocalDateTime getCreatedOn() {
                return LocalDateTime.now();
            }

            public boolean isUpdated() {
                return false;
            }
        };

        CommentShort comment2 = new CommentShort() {
            public long getId() {
                return 2L;
            }

            public String getText() {
                return "Comment 2";
            }

            public String getAuthorName() {
                return "Author 2";
            }

            public LocalDateTime getCreatedOn() {
                return LocalDateTime.now();
            }

            public boolean isUpdated() {
                return false;
            }
        };
        Page<CommentShort> commentPage = new PageImpl<>(List.of(comment1, comment2), Pageable.unpaged(), 2);
        BDDMockito.given(commentRepository.findAllByEventId(eventPersisted.getId(), Pageable.unpaged())).willReturn(commentPage);

        List<CommentShort> result = commentServiceTest.getAll(eventPersisted.getId(), Pageable.unpaged());

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test get by id functionality")
    void givenEventIdAndCommentId_whenGetCommentById_thenCommentShortIsReturned() {
        Event eventPersisted = DataUtils.getEventPersisted(
                DataUtils.getCategoryPersisted(),
                DataUtils.getUserInitiatorPersisted(),
                DataUtils.getLocationPersisted()
        );
        BDDMockito.given(eventRepository.findById(anyLong())).willReturn(Optional.of(eventPersisted));

        CommentShort comment1 = new CommentShort() {
            public long getId() {
                return 1L;
            }

            public String getText() {
                return "Comment 1";
            }

            public String getAuthorName() {
                return "Author 1";
            }

            public LocalDateTime getCreatedOn() {
                return LocalDateTime.now();
            }

            public boolean isUpdated() {
                return false;
            }
        };

        BDDMockito.given(commentRepository.findByEventIdAndId(eventPersisted.getId(), comment1.getId())).willReturn(comment1);

        CommentShort result = commentServiceTest.getCommentById(eventPersisted.getId(), comment1.getId());

        assertThat(result).isNotNull();
    }
}