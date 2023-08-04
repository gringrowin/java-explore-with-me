package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;


import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Validated
@Slf4j
public class EventPrivateController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllEventsByPrivateAccess(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("EventPrivateController.getAllEventsByPrivateAccess: {}  - Started", userId);
        List<EventShortDto> eventShortDtoList =
                eventService.getAllEventsByPrivateAccess(userId,from, size);
        log.info("EventPrivateController.getAllEventsByPrivateAccess: {}  - Finished", eventShortDtoList.size());
        return eventShortDtoList;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEventByPrivateAccess(@PathVariable Long userId,
                                            @RequestBody @Valid NewEventDto newEventDto) {
        log.info("EventPrivateController.createEventByPrivateAccess: {}, {}  - Started", userId, newEventDto);
        EventFullDto eventFullDto = eventService.createEventByPrivateAccess(userId, newEventDto);
        log.info("EventPrivateController.createEventByPrivateAccess: {} - Finished", eventFullDto);
        return eventService.createEventByPrivateAccess(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByPrivateAccess(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info("EventPrivateController.getEventByPrivateAccess: {}, {}  - Started", userId, eventId);
        EventFullDto eventFullDto = eventService.getEventByPrivateAccess(userId, eventId);
        log.info("EventPrivateController.getEventByPrivateAccess: {} - Finished", eventFullDto);
        return eventFullDto;
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByPrivate(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("EventPrivateController.updateEventByPrivate: " +
                "{}, {}, {}  - Started", userId, eventId, updateEventUserRequest);
        EventFullDto eventFullDto = eventService.updateEventByPrivateAccess(userId, eventId, updateEventUserRequest);
        log.info("EventPrivateController.updateEventByPrivate: {} - Finished", eventFullDto);
        return eventFullDto;
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequestsByEventInitiator(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info("EventPrivateController.getEventRequestsByEventInitiator: {}, {}  - Started", userId, eventId);

        List<ParticipationRequestDto> requestDtoList =
                requestService.getEventRequestsByEventInitiator(userId, eventId);

        log.info("EventPrivateController.getEventRequestsByEventInitiator: " +
                "{} - Finished", requestDtoList);
        return requestDtoList;
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult patchEventRequestsByEventInitiator(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("EventPrivateController.patchEventRequestsByEventInitiator: " +
                "{}, {}, {} - Started", userId, eventId, eventRequestStatusUpdateRequest);

        EventRequestStatusUpdateResult result = requestService
                .patchEventRequestsByEventInitiator(userId, eventId, eventRequestStatusUpdateRequest);

        log.info("EventPrivateController.patchEventRequestsByEventInitiator: {} - Started", result);
        return result;
    }
}
