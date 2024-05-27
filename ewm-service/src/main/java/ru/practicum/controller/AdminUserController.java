package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.service.UserService;
import ru.practicum.util.OffsetPageRequest;

import java.util.List;

@Slf4j
@RestController("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

	private final UserService userService;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<UserDto> getUsers(@RequestParam List<Long> ids,
								  @RequestParam int from,
								  @RequestParam int size) {
		log.info("Get users request: ids: {}, from: {}, size; {}", ids, from, size);
		PageRequest page = OffsetPageRequest.createPageRequest(from, size);
		List<UserDto> users = userService.getUsers(ids, page);
		log.info("Get users response: found {} users", users.size());
		return users;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UserDto createNewUser(@RequestBody NewUserRequest newUserRequest) {
		log.info("New user create request: {}", newUserRequest);
		UserDto savedUser = userService.createNewUser(newUserRequest);
		log.info("New user created: {}", savedUser);
		return savedUser;
	}

	@DeleteMapping("/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable Long userId){
		log.info("Delete user request, userId: {}", userId);
		userService.deleteUser(userId);
	}

}
