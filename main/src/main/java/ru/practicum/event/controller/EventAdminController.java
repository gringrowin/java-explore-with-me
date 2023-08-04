package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Validated
@Slf4j
public class EventAdminController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEventsByAdminAccess(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("EventAdminController.getEventsByAdminAccess: " +
                "{} - users, " +
                "{} - states, " +
                "{} - categories, " +
                "{} - rangeStart, " +
                "{} - rangeEnd, " +
                "{} - from, " +
                "{} size " +
                "- Started", users, states, categories, rangeStart, rangeEnd, from, size);
        List<EventFullDto> eventFullDtoList =
                eventService.getEventsByAdminAccess(users, states, categories, rangeStart, rangeEnd, from, size);
        log.info("EventAdminController.getEventsByAdminAccess: {},  - Finished", eventFullDtoList.size());
        return eventFullDtoList;
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByAdminAccess(@PathVariable Long eventId,
                                          @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("EventAdminController.patchEventByAdmin: {}, {}  - Started", eventId, updateEventAdminRequest);
        EventFullDto eventFullDto = eventService.updateEventByAdminAccess(eventId, updateEventAdminRequest);
        log.info("EventAdminController.patchEventByAdmin: {} - Finished", eventFullDto);
        return eventFullDto;
    }
}
