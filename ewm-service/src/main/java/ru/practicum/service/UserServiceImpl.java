package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserDtoMapper;
import ru.practicum.exception.UserUniqueEmailViolationException;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	@Override
	public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
		Page<User> users;
		if (ids != null && !ids.isEmpty()) {
			users = userRepository.findByIds(ids, pageable);
		} else {
			users = userRepository.findAll(pageable);
		}

		return users.getContent().stream()
				.map(UserDtoMapper.INSTANCE::userToUserDto)
				.collect(Collectors.toList());
	}

	@Override
	public UserDto createNewUser(NewUserRequest newUserRequest) {
		Optional<User> existingUser = userRepository.findByEmail(newUserRequest.getEmail());
		if (existingUser.isPresent()) {
			throw new UserUniqueEmailViolationException("Attempt to create user with email '" + newUserRequest.getEmail() + "' failed");
		}
		User savedUser = userRepository.save(UserDtoMapper.INSTANCE.newUserRequestToUser(newUserRequest));
		return UserDtoMapper.INSTANCE.userToUserDto(savedUser);
	}

	@Override
	public void deleteUser(long userId) {
		userRepository.deleteById(userId);
	}
}
