package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.dto.*;
import ru.practicum.event.enums.EventSortBy;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.LocationMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.ForbiddenParameterException;
import ru.practicum.exception.IvalidDataTimeException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final LocationRepository locationRepository;

    private final UserService userService;

    private final CategoryService categoryService;

    private final StatsService statsService;

    private final EventMapper eventMapper;

    private final LocationMapper locationMapper;


    @Override
    @Transactional
    public EventFullDto createEventByPrivateAccess(Long userId, NewEventDto newEventDto) {
        log.info("EventServiceImpl.createEventByPrivateAccess: {}, {} - Started", userId, newEventDto);

        if (newEventDto.getEventDate() != null
                && newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IvalidDataTimeException(
                    String.format("Field: eventDate. Error: должно содержать время, " +
                            "не ранее чем через 2 часа от текущего. " +
                            "Value: %s", newEventDto.getEventDate()));
        }

        User eventOwner = userService.findUserById(userId);
        Category eventCategory = categoryService.findCategoryById(newEventDto.getCategory());
        Location eventLocation = saveLocation(newEventDto.getLocation());

        Event event = eventMapper.toEvent(
                newEventDto,
                eventOwner,
                eventCategory,
                eventLocation,
                LocalDateTime.now(),
                EventState.PENDING,
                0L,
                0L);

        event = eventRepository.save(event);

        EventFullDto eventFullDto = eventMapper.toEventFullDto(event, statsService.getViewsByEvent(event));
        log.info("EventServiceImpl.createEventByPrivateAccess: {} - Finished", eventFullDto);
        return eventFullDto;
    }


    @Override
    @Transactional
    public EventFullDto updateEventByAdminAccess(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("EventServiceImpl.updateEventByAdminAccess: {}, {} - Started", eventId, updateEventAdminRequest);
        if (updateEventAdminRequest.getEventDate() != null
                && updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new IvalidDataTimeException(
                    String.format("Field: eventDate. Error: должно содержать время, " +
                            "не ранее чем через 1 час от текущего." +
                            "  Value: %s", updateEventAdminRequest.getEventDate()));
        }

        Event event = findEventById(eventId);

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(categoryService.findCategoryById(updateEventAdminRequest.getCategory()));
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(saveLocation(updateEventAdminRequest.getLocation()));
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            if (updateEventAdminRequest.getParticipantLimit() != 0
                    && event.getConfirmedRequests() != null
                    && (updateEventAdminRequest.getParticipantLimit() <= event.getConfirmedRequests())) {
                throw new ForbiddenParameterException(
                        String.format("Новый лимит участников должен " +
                                        "быть больше либо равен количеству уже одобренных заявок: %s",
                                event.getConfirmedRequests()));
            }
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (!event.getState().equals(EventState.PENDING)) {
                throw new ForbiddenParameterException(
                        String.format("Можно опубликовать только события, " +
                                "которые ждут публикации: %s", event.getState()));
            }

            switch (updateEventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.REJECTED);
                    break;
            }
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        event = eventRepository.save(event);

        EventFullDto eventFullDto = eventMapper.toEventFullDto(event, statsService.getViewsByEvent(event));
        log.info("EventServiceImpl.updateEventByAdminAccess: {} - Finished", eventFullDto);
        return eventFullDto;
    }

    @Override
    public List<EventFullDto> getEventsByAdminAccess(
            List<Long> users,
            List<EventState> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Integer from,
            Integer size) {

        log.info("EventServiceImpl.getEventsByAdminAccess: " +
                "{} - users, " +
                "{} - states, " +
                "{} - categories, " +
                "{} - rangeStart," +
                " {} - rangeEnd," +
                "{} - from," +
                "{} size" +
                "  - Started", users, states, categories, rangeStart, rangeEnd, from, size);

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new IvalidDataTimeException(
                    String.format("Некорректные параметры временного " +
                    "интервала. Value: rangeStart = %s, rangeEnd = %s", rangeStart, rangeEnd));
        }

        List<Event> events = eventRepository.getEventsByAdminAccess(
                users, states, categories, rangeStart, rangeEnd, from, size);

        List<EventFullDto> eventDtos = new ArrayList<>();
        events.forEach(event -> eventDtos.add(eventMapper.toEventFullDto(event, statsService.getViewsByEvent(event))));
        log.info("EventServiceImpl.getEventsByAdminAccess: {},  - Finished", eventDtos.size());
        return eventDtos;
    }

    @Override
    public List<EventShortDto> getEventsByPublicAccess(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            EventSortBy sort,
            Integer from,
            Integer size,
            HttpServletRequest request) {
        log.info("EventServiceImpl.getEventsByPublicAccess: " +
                "{} - text, " +
                "{} - categories, " +
                "{} - paid, " +
                "{} - rangeStart, " +
                "{} - rangeEnd, " +
                "{} - onlyAvailable, " +
                "{} - sort, " +
                "{} - from," +
                "{} size" +
                "  - Started", text, categories, paid, onlyAvailable, sort, rangeStart, rangeEnd, from, size);

        if (rangeStart
                != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new IvalidDataTimeException(
                    String.format("Некорректные параметры временного " +
                            "интервала. Value: rangeStart = %s, rangeEnd = %s", rangeStart, rangeEnd));
        }

        List<Event> events = eventRepository.getEventsByPublicAccess(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        List<EventShortDto> eventDtos = new ArrayList<>();
        statsService.addHit(request);
        events.forEach(
                event -> eventDtos.add(eventMapper.toEventShortDto(event, statsService.getViewsByEvent(event))));


        log.info("EventServiceImpl.getEventsByPublicAccess: {} - Finished", eventDtos.size());
        return eventDtos;
    }

    @Override
    public EventFullDto getEventByPublicAccess(Long eventId, HttpServletRequest request) {
        log.info("EventServiceImpl.getEventByPublicAccess: {} - Started", eventId);
        Event event = findEventById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException(String.format("Событие с ID: %s не опубликовано", eventId));
        }
        statsService.addHit(request);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event, statsService.getViewsByEvent(event));
        log.info("EventServiceImpl.getEventByPublicAccess: {} - Finished", eventFullDto);
        return eventFullDto;
    }

    @Override
    public EventFullDto getEventByPrivateAccess(Long userId, Long eventId) {
        log.info("EventServiceImpl.getEventByPrivateAccess: {}, {} - Started", userId, eventId);
        User user = userService.findUserById(userId);
        Event event = findEventById(eventId);
        if (!event.getInitiator().equals(user)) {
            throw new NotFoundException("Пользователь не является инициатором события");
        }
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event, statsService.getViewsByEvent(event));
        log.info("EventServiceImpl.getEventByPrivateAccess: {} - Finished", eventFullDto);
        return eventFullDto;
    }

    @Override
    public EventFullDto updateEventByPrivateAccess(
            Long userId,
            Long eventId,
            UpdateEventUserRequest updateEventUserRequest) {
        log.info("EventServiceImpl.updateEventByPrivateAccess: " +
                "{}, {}, {} - Started", userId, eventId, updateEventUserRequest);

        if (updateEventUserRequest.getEventDate() != null
                && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IvalidDataTimeException(
                    String.format("Field: eventDate. Error: должно содержать время, " +
                            "не ранее чем через 2 часа от текущего." +
                            "  Value: %s", updateEventUserRequest.getEventDate()));
        }

        User user = userService.findUserById(userId);
        Event event = findEventById(eventId);
         if (!user.equals(event.getInitiator())) {
             throw new ForbiddenParameterException("Нельзя изменять чужое событие");
         }

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenParameterException("Нельзя изменять опубликованые события");
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(categoryService.findCategoryById(updateEventUserRequest.getCategory()));
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(saveLocation(updateEventUserRequest.getLocation()));
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event, statsService.getViewsByEvent(event));
        log.info("EventServiceImpl.updateEventByPrivateAccess: {} - Finished", eventFullDto);
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getAllEventsByPrivateAccess(Long userId, Integer from, Integer size) {
        log.info("EventServiceImpl.getAllEventsByPrivateAccess: {} - Started", userId);
        userService.findUserById(userId);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size));
        List<EventShortDto> eventDtos = new ArrayList<>();
        events.forEach(event -> eventDtos.add(eventMapper.toEventShortDto(event, statsService.getViewsByEvent(event))));
        log.info("EventServiceImpl.getAllEventsByPrivateAccess: {} - Finished", eventDtos.size());
        return eventDtos;
    }

    public Event updateConfirmedRequestsInEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public List<Event> getEventsByIds(List<Long> eventsIds) {
        log.info("EventServiceImpl.getEventsByIds: {} - Started", eventsIds);
        if (eventsIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Event> events = eventRepository.findAllByIdIn(eventsIds);
        log.info("EventServiceImpl.getEventsByIds: {} - Started", events);
        return events;
    }

    public Event findEventById(Long eventId) {
        log.info("EventServiceImpl.findEventById: {} ", eventId);
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(
                        String.format("Событие с ID : %s не найдено", eventId))
        );
    }

    private Location saveLocation(LocationDto locationDto) {
        Location location = locationMapper.toLocation(locationDto);
        return locationRepository.findByLatitudeAndLongitude(location.getLatitude(), location.getLongitude())
                .orElseGet(() -> locationRepository.save(location));
    }

}
