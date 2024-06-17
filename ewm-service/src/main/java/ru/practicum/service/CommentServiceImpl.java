package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentDtoMapper commentDtoMapper;

    @Override
    public CommentDto create(NewCommentDto createDto, long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id='" + eventId + "' not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id='" + userId + "' not found"));

        Comment comment = commentDtoMapper.newCommentDtoToComment(createDto);
        comment.setEvent(event);
        comment.setAuthor(user);
        comment.setCreatedOn(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        return commentDtoMapper.commentToCommentDto(savedComment);
    }

    @Override
    public CommentDto updateByAuthor(NewCommentDto createDto, long userId, long eventId, long commentId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id='" + eventId + "' not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id='" + userId + "' not found"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("Comment with id='" + commentId + "' not found"));

        if (comment.getAuthor().getId() != userId) {
            throw new CommentUpdateNotByAuthorException("Only author can update a comment");
        }

        LocalDateTime createdOn = comment.getCreatedOn();
        if (createdOn.plusHours(1).isBefore(LocalDateTime.now())) {
            throw new CommentUpdateTimeLimitExceededException("You can update your comment only during 1 hour after posting it");
        }

        comment.setUpdated(true);
        comment.setText(createDto.getText());
        return commentDtoMapper.commentToCommentDto(commentRepository.save(comment));
    }

    @Override
    public void deleteByAuthor(long userId, long eventId, long commentId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id='" + eventId + "' not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id='" + userId + "' not found"));

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("Comment with id='" + commentId + "' not found"));
        if (comment.getAuthor().getId() != userId) {
            throw new CommentUpdateNotByAuthorException("Only author can update a comment");
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteByAdmin(long eventId, long commentId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id='" + eventId + "' not found"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("Comment with id='" + commentId + "' not found"));
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentShort> getAll(long eventId, Pageable pageable) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id='" + eventId + "' not found"));
        List<CommentShort> comments = commentRepository.findAllByEventId(eventId, pageable).toList();
        return comments;
    }

    @Override
    public CommentShort getCommentById(long eventId, long commentId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id='" + eventId + "' not found"));
        CommentShort comment = commentRepository.findByEventIdAndId(eventId, commentId);
        return comment;
    }
}
