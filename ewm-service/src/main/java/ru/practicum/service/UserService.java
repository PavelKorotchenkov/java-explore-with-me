package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {
	UserDto createNew(NewUserRequest newUserRequest);

	List<UserDto> getAll(List<Long> ids, Pageable pageable);

	void delete(long userId);

}
