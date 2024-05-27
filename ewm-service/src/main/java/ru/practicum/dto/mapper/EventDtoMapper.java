package ru.practicum.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.LocationDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper
public interface EventDtoMapper {

	EventDtoMapper INSTANCE = Mappers.getMapper(EventDtoMapper.class);

	@Mapping(source = "category", target = "category", qualifiedByName = "categoryIdToCategory")
	@Mapping(source = "eventDate", target = "eventDate", qualifiedByName = "stringToLocalDateTime")
	@Mapping(source = "location", target = "location", qualifiedByName = "locationDtoToLocation")
	Event newEventDtoToEvent(NewEventDto newEventDto);

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
}
