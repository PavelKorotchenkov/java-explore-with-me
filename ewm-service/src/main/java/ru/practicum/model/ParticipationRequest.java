package ru.practicum.model;

import lombok.*;
import ru.practicum.enums.ParticipationRequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
@Entity
@Builder
@Table(name = "participation_requests", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"event_id", "requester_id"})
})
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participation_request_id")
    private Long id;

    @Column(name = "created")
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ParticipationRequestStatus status;
}

