package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.comment.CommentShort;
import ru.practicum.model.Comment;

import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<CommentShort> findAllByEventId(long eventId, Pageable pageable);

    CommentShort findByEventIdAndId(long eventId, long commentId);

    @Query("SELECT c.event.id, COUNT(c) FROM Comment c " +
            "WHERE c.event.id IN :eventIds " +
            "GROUP BY c.event.id")
    List<Object[]> countByEventId(@Param("eventIds") Set<Long> eventIds);
}
