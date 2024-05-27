package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.EventShortDto;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

	@Query("SELECT e FROM Event e " +
			"WHERE (:text IS NULL OR e.text LIKE %:text%) " +
			"AND (:categories IS NULL OR e.category.id IN :categories) " +
			"AND (:paid IS NULL OR e.paid = :paid) " +
			"AND (:rangeStart IS NULL OR e.date >= :rangeStart) " +
			"AND (:rangeEnd IS NULL OR e.date <= :rangeEnd) " +
			"AND (:onlyAvailable IS NULL OR e.available = :onlyAvailable)")
	Page<EventShortDto> findPublicEvents(@Param("text") String text,
								 @Param("categories") List<Integer> categories,
								 @Param("paid") Boolean paid,
								 @Param("rangeStart") LocalDateTime rangeStart,
								 @Param("rangeEnd") LocalDateTime rangeEnd,
								 @Param("onlyAvailable") Boolean onlyAvailable,
								 Pageable pageable);
}

