package ru.practicum.dto.participation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ParticipationRequestMapper {

	@Mapping(source = "event", target = "event", qualifiedByName = "eventIdToEvent")
	@Mapping(source = "requester", target = "requester", qualifiedByName = "userIdToUser")
	@Mapping(source = "created", target = "created", qualifiedByName = "stringToLocalDateTime")
	@Mapping(source = "status", target = "status")
	ParticipationRequest toEntity(ParticipationRequestDto participationRequestDto);

	@Mapping(source = "participationRequest.event.id", target = "event")
	@Mapping(source = "participationRequest.requester.id", target = "requester")
	@Mapping(source = "participationRequest.created", target = "created", qualifiedByName = "localDateTimeToString")
	@Mapping(source = "participationRequest.status", target = "status")
	ParticipationRequestDto toDto(ParticipationRequest participationRequest);

	@Named("eventIdToEvent")
	static Event eventIdToEvent(Long eventId) {
		if (eventId == null) {
			return null;
		}
		Event event = new Event();
		event.setId(eventId);
		return event;
	}

	@Named("userIdToUser")
	static User userIdToUser(Long userId) {
		if (userId == null) {
			return null;
		}
		User user = new User();
		user.setId(userId);
		return user;
	}

	@Named("stringToLocalDateTime")
	static LocalDateTime stringToLocalDateTime(String dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return LocalDateTime.parse(dateTime, formatter);
	}

	@Named("localDateTimeToString")
	default String localDateTimeToString(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

}
