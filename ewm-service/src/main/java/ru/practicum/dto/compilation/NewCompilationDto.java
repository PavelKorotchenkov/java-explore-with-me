package ru.practicum.dto.compilation;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class NewCompilationDto {
    private Set<Long> events;
    private boolean pinned = false;
    @Size(min = 1, max = 50)
    @NotBlank
    @NotEmpty
    private String title;
}
