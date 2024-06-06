package ru.practicum.enums;

import java.util.Optional;

public enum EventStateAction {
    PUBLISH_EVENT, REJECT_EVENT, CANCEL_REVIEW;

    public static Optional<EventStateAction> getState(String stringState) {
        for (EventStateAction state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
