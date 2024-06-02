package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
	List<ParticipationRequest> findByRequesterId(long requesterId);

	List<ParticipationRequest> findByEventIdAndRequesterIdNot(long eventId, long userId);

	Optional<ParticipationRequest> findByEventIdAndRequesterId(long eventId, long userId);
}
