package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.participation.ParticipantCountDto;
import ru.practicum.enums.ParticipationRequestStatus;
import ru.practicum.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findByRequesterId(long requesterId);

    List<ParticipationRequest> findByEventIdAndRequesterIdNot(long eventId, long userId);

    Optional<ParticipationRequest> findByEventIdAndRequesterId(long eventId, long userId);

    @Query("SELECT new ru.practicum.dto.participation.ParticipantCountDto (pr.event.id, COUNT(pr)) " +
            "FROM ParticipationRequest pr " +
            "WHERE pr.event.id IN :eventIds " +
            "AND pr.status = :status GROUP BY pr.event.id")
    List<ParticipantCountDto> countParticipantsInAndStatus(@Param("eventIds") Set<Long> eventIds, @Param("status") ParticipationRequestStatus status);

    Long countByEventIdAndStatus(long eventId, ParticipationRequestStatus status);
}
