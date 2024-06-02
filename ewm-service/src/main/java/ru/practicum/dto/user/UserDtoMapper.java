package ru.practicum.dto.user;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.model.User;

@Mapper
public interface UserDtoMapper {
	UserDtoMapper INSTANCE = Mappers.getMapper(UserDtoMapper.class);

	User newUserRequestToUser(NewUserRequest newUserRequest);

	UserDto userToUserDto(User user);
}
