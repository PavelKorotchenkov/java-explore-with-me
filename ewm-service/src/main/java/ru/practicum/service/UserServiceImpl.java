package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.mapper.UserDtoMapper;
import ru.practicum.dto.user.UserDto;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

	private final UserRepository userRepository;
	@Override
	public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
		PageRequest page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

		return userRepository.findByIds(ids, page)
				.stream()
				.map(UserDtoMapper.INSTANCE::userToUserDto)
				.collect(Collectors.toList());
	}


	@Override
	public UserDto createNewUser(NewUserRequest newUserRequest) {
		User savedUser = userRepository.save(UserDtoMapper.INSTANCE.newUserRequestToUser(newUserRequest));
		return UserDtoMapper.INSTANCE.userToUserDto(savedUser);
	}

	@Override
	public void deleteUser(long userId) {
		userRepository.deleteById(userId);
	}
}
