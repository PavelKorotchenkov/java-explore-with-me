package ru.practicum.dto.comment;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class NewCommentDto {
    @Size(min = 1, max = 2000)
    @NotEmpty
    private String text;
    private boolean updated = false;
}
