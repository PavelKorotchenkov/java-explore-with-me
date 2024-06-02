package ru.practicum.dto.compilation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.Category;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface CompilationDtoMapper {
	CompilationDtoMapper INSTANCE = Mappers.getMapper(CompilationDtoMapper.class);

	@Mapping(source = "events", target = "events", qualifiedByName = "eventIdsToEvents")
	Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto);

	@Mapping(source = "events", target = "events", qualifiedByName = "eventsToEventShortDtos")
	CompilationDto compilationToCompilationDto(Compilation compilation);

	@Named("eventIdsToEvents")
	static Set<Event> eventIdsToEvents(Set<Long> eventIds) {
		if (eventIds == null) {
			return null;
		}
		return eventIds.stream().map(id -> {
			Event event = new Event();
			event.setId(id);
			return event;
		}).collect(Collectors.toSet());
	}

	@Named("eventsToEventShortDtos")
	static Set<EventShortDto> eventsToEventShortDtos(Set<Event> events) {
		if (events == null) {
			return null;
		}
		return events.stream().map(CompilationDtoMapper.INSTANCE::eventToEventShortDto).collect(Collectors.toSet());
	}

	@Mapping(target = "eventDate", source = "event.eventDate", qualifiedByName = "localDateTimeToString")
	@Mapping(target = "category", source = "event.category", qualifiedByName = "categoryToCategoryDto")
	@Mapping(target = "initiator", source = "event.initiator", qualifiedByName = "userToUserShortDto")
	EventShortDto eventToEventShortDto(Event event);

	@Named("categoryToCategoryDto")
	static CategoryDto categoryToCategoryDto(Category category) {
		if (category == null) {
			return null;
		}
		CategoryDto dto = new CategoryDto();
		dto.setId(category.getId());
		dto.setName(category.getName());
		return dto;
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
	static String localDateTimeToString(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
}