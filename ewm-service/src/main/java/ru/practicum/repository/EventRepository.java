package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.EventState;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

	@Query("SELECT e FROM Event e " +
			"WHERE (:text IS NULL OR LOWER(e.annotation) LIKE %:text% OR LOWER(e.description) LIKE %:text%) " +
			"AND (:categories IS NULL OR e.category.id IN :categories) " +
			"AND (:paid IS NULL OR e.paid = :paid) " +
			"AND (e.eventDate >= :rangeStart) " +
			"AND (e.eventDate <= :rangeEnd) " +
			"AND (e.participantLimit > e.confirmedRequests) " +
			"AND (:state = e.state) " +
			"ORDER BY e.eventDate DESC")
	List<Event> findAvailablePublicEvents(@Param("text") String text,
										  @Param("categories") List<Long> categories,
										  @Param("paid") Boolean paid,
										  @Param("rangeStart") LocalDateTime rangeStart,
										  @Param("rangeEnd") LocalDateTime rangeEnd,
										  @Param("state") EventState state);

	@Query("SELECT e FROM Event e " +
			"WHERE (:text IS NULL OR LOWER(e.annotation) LIKE %:text% OR LOWER(e.description) LIKE %:text%) " +
			"AND (:categories IS NULL OR e.category.id IN :categories) " +
			"AND (:paid IS NULL OR e.paid = :paid) " +
			"AND (e.eventDate >= :rangeStart) " +
			"AND (e.participantLimit > e.confirmedRequests) " +
			"AND (:state = e.state) " +
			"ORDER BY e.eventDate DESC")
	List<Event> findAvailablePublicEventsWithoutEndDate(@Param("text") String text,
										  @Param("categories") List<Long> categories,
										  @Param("paid") Boolean paid,
										  @Param("rangeStart") LocalDateTime rangeStart,
										  @Param("state") EventState state);

	@Query("SELECT e FROM Event e " +
			"WHERE (:text IS NULL OR LOWER(e.annotation) LIKE %:text% OR LOWER(e.description) LIKE %:text%) " +
			"AND (:categories IS NULL OR e.category.id IN :categories) " +
			"AND (:paid IS NULL OR e.paid = :paid) " +
			"AND (e.eventDate >= :rangeStart) " +
			"AND (e.eventDate <= :rangeEnd) " +
			"AND (:state = e.state) " +
			"ORDER BY e.eventDate DESC")
	List<Event> findAllPublicEvents(@Param("text") String text,
									@Param("categories") List<Long> categories,
									@Param("paid") Boolean paid,
									@Param("rangeStart") LocalDateTime rangeStart,
									@Param("rangeEnd") LocalDateTime rangeEnd,
									@Param("state") EventState state);

	@Query("SELECT e FROM Event e " +
			"WHERE (:text IS NULL OR LOWER(e.annotation) LIKE %:text% OR LOWER(e.description) LIKE %:text%) " +
			"AND (:categories IS NULL OR e.category.id IN :categories) " +
			"AND (:paid IS NULL OR e.paid = :paid) " +
			"AND (e.eventDate >= :rangeStart) " +
			"AND (:state = e.state) " +
			"ORDER BY e.eventDate DESC")
	List<Event> findAllPublicEventsWithoutEndDate(@Param("text") String text,
									@Param("categories") List<Long> categories,
									@Param("paid") Boolean paid,
									@Param("rangeStart") LocalDateTime rangeStart,
									@Param("state") EventState state);


	Set<Event> findByIdIn(Set<Long> ids);


	@Query("SELECT e FROM Event e " +
			"WHERE e.initiator.id = :initiatorId")
	Page<Event> findEventsByInitiatorId(@Param("initiatorId") long initiatorId, Pageable pageable);

	@Query("SELECT e FROM Event e " +
			"WHERE (:users IS NULL OR e.initiator.id IN :users) " +
			"AND (:states IS NULL OR e.state IN :states) " +
			"AND (:categories IS NULL OR e.category.id IN :categories) " +
			"AND (e.eventDate >= :rangeStart) " +
			"AND (e.eventDate <= :rangeEnd)")
	Page<Event> findAllEventsForAdmin(@Param("users") List<Long> users,
									  @Param("states") List<EventState> states,
									  @Param("categories") List<Long> categories,
									  @Param("rangeStart") LocalDateTime rangeStart,
									  @Param("rangeEnd") LocalDateTime rangeEnd,
									  Pageable pageable);

	@Query("SELECT e FROM Event e " +
			"WHERE (:users IS NULL OR e.initiator.id IN :users) " +
			"AND (:states IS NULL OR e.state IN :states) " +
			"AND (:categories IS NULL OR e.category.id IN :categories) " +
			"AND (e.eventDate >= :rangeStart)")
	Page<Event> findAllEventsWithoutEndDateForAdmin(@Param("users") List<Long> users,
									  @Param("states") List<EventState> states,
									  @Param("categories") List<Long> categories,
									  @Param("rangeStart") LocalDateTime rangeStart,
									  Pageable pageable);

	@Query("SELECT e FROM Event e WHERE e.category.id = :categoryId")
	Optional<Event> findFirstByCategoryId(@Param("categoryId") long categoryId);
}

