package ru.practicum.event.service;

import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void addHit(HttpServletRequest request);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    Long getViewsByEvent(Event event);
}
