package ru.practicum.dto.user;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserDtoMapper {

	User newUserRequestToUser(NewUserRequest newUserRequest);

	UserDto userToUserDto(User user);
}
