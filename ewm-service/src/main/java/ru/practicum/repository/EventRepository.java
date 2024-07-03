package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;

import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Set<Event> findByIdIn(Set<Long> ids);

    Page<Event> findByInitiatorId(long initiatorId, Pageable pageable);

    Optional<Event> findFirstByCategoryId(@Param("categoryId") long categoryId);
}

