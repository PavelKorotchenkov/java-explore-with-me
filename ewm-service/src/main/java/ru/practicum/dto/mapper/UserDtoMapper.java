package ru.practicum.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.model.User;

@Mapper
public interface UserDtoMapper {
	UserDtoMapper INSTANCE = Mappers.getMapper(UserDtoMapper.class);

	User newUserRequestToUser(NewUserRequest newUserRequest);
	UserDto userToUserDto(User user);
}
