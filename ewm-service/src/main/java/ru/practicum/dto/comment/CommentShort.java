package ru.practicum.dto.comment;

import java.time.LocalDateTime;

public interface CommentShort {
    long getId();

    String getText();

    String getAuthorName();

    LocalDateTime getCreatedOn();

    boolean isUpdated();
}
