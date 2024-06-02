package ru.practicum.enums;

import java.util.Optional;

public enum EventState {
	PENDING, PUBLISHED, CANCELED;

	public static Optional<EventState> getState(String stringState) {
		for (EventState state : values()) {
			if (state.name().equalsIgnoreCase(stringState)) {
				return Optional.of(state);
			}
		}
		return Optional.empty();
	}
}
