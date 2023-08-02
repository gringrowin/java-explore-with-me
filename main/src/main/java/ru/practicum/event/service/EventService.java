package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.event.enums.EventSortBy;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto updateEventByAdminAccess(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventFullDto> getEventsByAdminAccess(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    List<EventShortDto> getEventsByPublicAccess(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, EventSortBy sort, Integer from, Integer size, HttpServletRequest request);

    EventFullDto getEventByPublicAccess(Long id, HttpServletRequest request);

    EventFullDto getEventByPrivateAccess(Long userId, Long eventId);

    EventFullDto updateEventByPrivateAccess(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto createEventByPrivateAccess(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getAllEventsByPrivateAccess(Long userId, Integer from, Integer size);

    Event findEventById(Long eventId);

    Event updateConfirmedRequestsInEvent(Event event);

    List<Event> getEventsByIds(List<Long> events);
}
