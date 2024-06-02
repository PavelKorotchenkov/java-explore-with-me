package ru.practicum.dto.location;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.model.Location;

@Mapper
public interface LocationDtoMapper {
	LocationDtoMapper INSTANCE = Mappers.getMapper(LocationDtoMapper.class);

	Location locationDtoToLocation(LocationDto locationDto);

	LocationDto locationToLocationDto(Location location);
}
