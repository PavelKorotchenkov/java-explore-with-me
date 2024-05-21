package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StatsDtoResponse {
	private String app;
	private String uri;
	private long hits;
}
