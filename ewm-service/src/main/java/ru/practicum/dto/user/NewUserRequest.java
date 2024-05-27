package ru.practicum.dto.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class NewUserRequest {
	@Email
	@Max(254)
	@Min(6)
	@NotBlank
	@NotNull
	private String email;

	@Max(250)
	@Min(2)
	@NotBlank
	@NotNull
	private String name;
}
