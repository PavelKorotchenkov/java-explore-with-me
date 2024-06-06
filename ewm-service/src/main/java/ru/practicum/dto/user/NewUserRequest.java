package ru.practicum.dto.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class NewUserRequest {
    @Email
    @Size(min = 6, max = 254)
    @NotBlank
    @NotEmpty
    private String email;

    @Size(min = 2, max = 250)
    @NotBlank
    @NotEmpty
    private String name;
}
