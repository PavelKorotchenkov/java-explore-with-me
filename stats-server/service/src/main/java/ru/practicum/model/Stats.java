package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stats")
public class Stats {
	@Id
	@Column(name = "stats_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String app;
	private String uri;
	private String ip;
	private LocalDateTime created;
}
