package ru.practicum.enums;

import java.util.Optional;

public enum ParticipationRequestStatus {
    PENDING, CONFIRMED, CANCELED, REJECTED;

    public static Optional<ParticipationRequestStatus> getStatus(String stringState) {
        for (ParticipationRequestStatus state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
