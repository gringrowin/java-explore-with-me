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
        log.info("StatsServiceImpl.addHit: " +
                "start = {}, end = {}, uris = {}, unique = {} - Started ", start, end, uris, unique);

        ResponseEntity<Object> response = statsClient.getStats(start, end, uris, unique);

        try {
            return Arrays.asList(mapper.readValue(mapper.writeValueAsString(response.getBody()), ViewStatsDto[].class));
        } catch (IOException exception) {
            throw new ClassCastException(exception.getMessage());
        }
    }

    @Override
    public Long getViewsByEvent(Event event) {
        log.info("StatsServiceImpl.getViews: {} - events.views - Started", event.getViews());

       Long views = 0L;

        if (event.getViews() == null && event.getPublishedOn() != null) {
            return views;
        }

        LocalDateTime start = LocalDateTime.now().minusYears(100L);
        LocalDateTime end = LocalDateTime.now();
        String uris = "/events/" + event.getId();

        List<ViewStatsDto> stats = getStats(start, end, List.of(uris), true);
        if (!stats.isEmpty()) {
            views = views + stats.get(0).getHits();
        }

        log.info("StatsServiceImpl.getViews: {} - views - Finished", views);
        return views;
    }
}
