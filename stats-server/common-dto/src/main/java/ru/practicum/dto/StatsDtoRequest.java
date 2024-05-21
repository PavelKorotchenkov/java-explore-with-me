package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StatsDtoRequest {
	private String app;
	private String uri;
	private String ip;
	private String timestamp;
}
