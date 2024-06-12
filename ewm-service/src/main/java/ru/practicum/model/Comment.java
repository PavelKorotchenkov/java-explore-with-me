package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private long id;
    private String text;
    @JoinColumn(name = "event_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Event event;
    @JoinColumn(name = "author_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private User author;
    @Column(name = "comment_created_on")
    private LocalDateTime createdOn;
    @Column(name = "updated")
    private Boolean updated;
}
