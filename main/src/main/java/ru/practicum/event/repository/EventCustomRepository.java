package ru.practicum.event.repository;

import ru.practicum.event.enums.EventSortBy;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventCustomRepository {

    List<Event> getEventsByAdminAccess(
            List<Long> users,
            List<EventState> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Integer from,
            Integer size);

    List<Event> getEventsByPublicAccess(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            EventSortBy sort,
            Integer from,
            Integer size);
}
