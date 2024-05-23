package ru.practicum.client;

import org.springframework.http.ResponseEntity;

public interface Client<T> {
	ResponseEntity<Object> saveStats(T parameters);

	ResponseEntity<Object> getStats(String start, String end, String uris, Boolean uniqueBoolean);
}
