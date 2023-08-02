package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.enums.EventSortBy;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
@Slf4j
public class EventPublicController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsByPublicAccess(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) EventSortBy sort,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size,
            HttpServletRequest request) {
        log.info("EventPublicController.getEventsByPublicAccess: " +
                "{} - text, " +
                "{} - categories, " +
                "{} - paid, " +
                "{} - rangeStart," +
                " {} - rangeEnd," +
                " {} - onlyAvailable," +
                " {} - sort," +
                "{} - from," +
                "{} size" +
                "  - Started", text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        List<EventShortDto> eventShortDtoList = eventService.getEventsByPublicAccess(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size, request);
        log.info("EventPublicController.getEventsByPublicAccess: {},  - Finished", eventShortDtoList.size());
        return eventShortDtoList;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByPublicAccess(@PathVariable Long id,
                                               HttpServletRequest request) {
        log.info("EventPublicController.getEventByPublicAccess: {},  - Started", id);
        EventFullDto eventFullDto = eventService.getEventByPublicAccess(id, request);
        log.info("EventPublicController.getEventByPublicAccess: {},  - Finished", eventFullDto);
        return eventFullDto;
    }
}
