package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {
	@Query("SELECT new ru.practicum.dto.StatsResponseDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
			"FROM Stats s " +
			"WHERE s.uri IN :uris " +
			"AND s.created BETWEEN :start AND :end " +
			"GROUP BY s.app, s.uri " +
			"ORDER BY COUNT(DISTINCT s.ip) DESC")
	List<StatsResponseDto> findStatsUniqueIp(@Param("start") LocalDateTime start,
											 @Param("end") LocalDateTime end,
											 @Param("uris") List<String> uris);

	@Query("SELECT new ru.practicum.dto.StatsResponseDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
			"FROM Stats s " +
			"WHERE s.created BETWEEN :start AND :end " +
			"GROUP BY s.app, s.uri " +
			"ORDER BY COUNT(DISTINCT s.ip) DESC")
	List<StatsResponseDto> findAllStatsUniqueIp(@Param("start") LocalDateTime start,
												@Param("end") LocalDateTime end);

	@Query("SELECT new ru.practicum.dto.StatsResponseDto(s.app, s.uri, COUNT(s.ip)) " +
			"FROM Stats s " +
			"WHERE s.uri IN :uris " +
			"AND s.created BETWEEN :start AND :end " +
			"GROUP BY s.app, s.uri " +
			"ORDER BY COUNT(s.ip) DESC")
	List<StatsResponseDto> findStatsAllIp(@Param("start") LocalDateTime start,
										  @Param("end") LocalDateTime end,
										  @Param("uris") List<String> uris);

	@Query("SELECT new ru.practicum.dto.StatsResponseDto(s.app, s.uri, COUNT(s.ip)) " +
			"FROM Stats s " +
			"WHERE s.created BETWEEN :start AND :end " +
			"GROUP BY s.app, s.uri " +
			"ORDER BY COUNT(s.ip) DESC")
	List<StatsResponseDto> findAllStatsAllIp(@Param("start") LocalDateTime start,
											 @Param("end") LocalDateTime end);
}
