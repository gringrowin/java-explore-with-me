package ru.practicum.event.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final StatsClient statsClient;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void addHit(HttpServletRequest request) {
        log.info("StatsServiceImpl.addHit: {} - ip - Started", request.getRemoteAddr());

        statsClient.addHit(
                "ewm-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now());
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("Отправлен запрос на получение статистики к серверу статистики с параметрами " +
                "start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);

        ResponseEntity<Object> response = statsClient.getStats(start, end, uris, unique);

        try {
            return Arrays.asList(mapper.readValue(mapper.writeValueAsString(response.getBody()), ViewStatsDto[].class));
        } catch (IOException exception) {
            throw new ClassCastException(exception.getMessage());
        }
    }

    @Override
    public Map<Long, Long> getViews(List<Event> events) {
        log.info("StatsServiceImpl.getViews: {} - events.size - Started", events.size());

        Map<Long, Long> views = new HashMap<>();

        if (events.isEmpty()) {
            return views;
        }

        List<Event> publishedEvents = events
                .stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());

        if (events.isEmpty()) {
            return views;
        }

        LocalDateTime start = LocalDateTime.MIN;
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = publishedEvents.stream()
                .map(Event::getId)
                .map(id -> ("/events/" + id))
                .collect(Collectors.toList());

        List<ViewStatsDto> stats = getStats(start, end, uris, null);
        stats.forEach(stat -> {
            Long eventId = Long.parseLong(stat.getUri()
                    .split("/", 0)[2]);
            views.put(eventId, views.getOrDefault(eventId, 0L) + stat.getHits());
        });

        log.info("StatsServiceImpl.getViews: {} - events.size - Finished", views);
        return views;
    }
}
