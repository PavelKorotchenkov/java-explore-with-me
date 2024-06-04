package ru.practicum.dto.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventDtoMapper {

	@Mapping(source = "category", target = "category", qualifiedByName = "categoryIdToCategory")
	@Mapping(source = "eventDate", target = "eventDate", qualifiedByName = "stringToLocalDateTime")
	@Mapping(source = "location", target = "location", qualifiedByName = "locationDtoToLocation")
	Event newEventDtoToEvent(NewEventDto newEventDto);

	@Mapping(target = "category", source = "event.category")
	@Mapping(target = "createdOn", source = "event.createdOn", qualifiedByName = "localDateTimeToString")
	@Mapping(target = "description", source = "event.description")
	@Mapping(target = "eventDate", source = "event.eventDate", qualifiedByName = "localDateTimeToString")
	@Mapping(target = "id", source = "event.id")
	@Mapping(target = "initiator", source = "event.initiator")
	@Mapping(target = "location", source = "event.location")
	@Mapping(target = "paid", source = "event.paid")
	@Mapping(target = "participantLimit", source = "event.participantLimit")
	@Mapping(target = "publishedOn", source = "event.publishedOn", qualifiedByName = "localDateTimeToString")
	@Mapping(target = "requestModeration", source = "event.requestModeration")
	@Mapping(target = "state", source = "event.state")
	@Mapping(target = "title", source = "event.title")
	EventFullDto eventToEventFullDto(Event event);

	@Mapping(target = "category", source = "event.category")
	@Mapping(target = "description", source = "event.description")
	@Mapping(target = "eventDate", source = "event.eventDate", qualifiedByName = "localDateTimeToString")
	@Mapping(target = "id", source = "event.id")
	@Mapping(target = "initiator", source = "event.initiator")
	@Mapping(target = "paid", source = "event.paid")
	@Mapping(target = "title", source = "event.title")
	EventShortDto eventToEventShortDto(Event event);

	@Named("categoryIdToCategory")
	static Category categoryIdToCategory(long categoryId) {
		Category category = new Category();
		category.setId(categoryId);
		return category;
	}

	@Named("stringToLocalDateTime")
	static LocalDateTime stringToLocalDateTime(String dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return LocalDateTime.parse(dateTime, formatter);
	}

	@Named("locationDtoToLocation")
	static Location locationDtoToLocation(LocationDto locationDto) {
		if (locationDto == null) {
			return null;
		}
		Location location = new Location();
		location.setLat(locationDto.getLat());
		location.setLon(locationDto.getLon());
		return location;
	}

	@Named("userToUserShortDto")
	static UserShortDto userToUserShortDto(User user) {
		if (user == null) {
			return null;
		}
		UserShortDto dto = new UserShortDto();
		dto.setId(user.getId());
		dto.setName(user.getName());
		return dto;
	}

	@Named("localDateTimeToString")
	default String localDateTimeToString(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
}
