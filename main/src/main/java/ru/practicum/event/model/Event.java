package ru.practicum.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.model.Category;
import ru.practicum.event.enums.EventState;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false, unique = true)
    private Long id;
    @Column(name = "event_title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "annotation")
    private String annotation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private EventState state;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "confirmed_requests")
    private Long confirmedRequests;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "views")
    private Long views;

    @Column(name = "request_moderation")
    private Boolean requestModeration;
}
