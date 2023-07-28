package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto create(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("StatsController.create: {} - Started", endpointHitDto);
        EndpointHitDto endpointHit = statsService.create(endpointHitDto);
        log.info("StatsController.create: {} - Finished", endpointHit);
        return endpointHit;
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false, name = "uris") Set<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique
    ) {
        log.info("StatsController.create: {}, {}, {}, {} - Started", start, end, uris, unique);
        List<ViewStatsDto> viewStatsDtoList = statsService.getStats(
                start,
                end,
                uris,
                unique);
        log.info("StatsController.create: {} - Finished", viewStatsDtoList.size());
        return viewStatsDtoList;
    }
}
