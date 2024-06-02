package ru.practicum.model;

import lombok.*;
import ru.practicum.enums.EventState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@Builder
@Entity
@Table(name = "events")
public class Event {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_id")
	private Long id;

	@Column(name = "event_annotation")
	private String annotation;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@Column(name = "event_confirmed_requests")
	private Long confirmedRequests;

	@Column(name = "event_created_on")
	private LocalDateTime createdOn;

	@Column(name = "event_description")
	private String description;

	@Column(name = "event_date")
	private LocalDateTime eventDate;

	@ManyToOne
	@JoinColumn(name = "initiator_id")
	private User initiator;

	@OneToOne
	@JoinColumn(name = "location_id")
	private Location location;

	@Column(name = "event_paid")
	private Boolean paid;

	@Column(name = "event_participant_limit")
	private Integer participantLimit;

	@Column(name = "event_published_on")
	private LocalDateTime publishedOn;

	@Column(name = "event_request_moderation")
	private Boolean requestModeration;

	@Column(name = "event_state")
	@Enumerated(EnumType.STRING)
	private EventState state;

	@Column(name = "event_title")
	private String title;
}
