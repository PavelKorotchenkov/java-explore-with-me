package ru.practicum.dto.location;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.model.Location;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationDtoMapper {

	Location locationDtoToLocation(LocationDto locationDto);

}
