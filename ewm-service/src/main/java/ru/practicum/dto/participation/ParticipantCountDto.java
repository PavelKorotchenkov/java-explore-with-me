package ru.practicum.dto.participation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantCountDto {
    private long eventId;
    private long confirmedRequestCount;
}
