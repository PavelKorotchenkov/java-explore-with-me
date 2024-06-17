package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentShort;
import ru.practicum.dto.comment.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto create(NewCommentDto createDto, long userId, long eventId);

    CommentDto updateByAuthor(NewCommentDto createDto, long userId, long eventId, long commentId);

    void deleteByAuthor(long userId, long eventId, long commentId);

    void deleteByAdmin(long eventId, long commentId);

    List<CommentShort> getAll(long eventId, Pageable pageable);

    CommentShort getCommentById(long eventId, long commentId);
}
