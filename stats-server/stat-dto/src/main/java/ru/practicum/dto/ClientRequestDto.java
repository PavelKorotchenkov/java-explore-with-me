package ru.practicum.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ClientRequestDto {
	private final LocalDateTime start;
	private final LocalDateTime end;
	private final List<String> uris;
	private final boolean unique;
}
