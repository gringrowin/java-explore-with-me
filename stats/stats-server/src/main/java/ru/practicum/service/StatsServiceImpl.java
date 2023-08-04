package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.Stats;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {

    private StatsRepository statsRepository;

    private final StatsMapper statsMapper;

    @Override
    @Transactional
    public EndpointHitDto create(EndpointHitDto endpointHitDto) {
        log.info("StatsServiceImpl.save: {} - Started", endpointHitDto);
        Stats stats = statsRepository.save(statsMapper.toStats(endpointHitDto));
        log.info("StatsServiceImpl.save: {} - Finished", stats);
        return statsMapper.toEndpointHitDto(stats);
    }

    @Override
    @Transactional
    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       Set<String> uris,
                                       Boolean unique) {
        log.info("StatsServiceImpl.getStats: {}, {}, {}, {} - Started", start, end, uris, unique);

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Недопустимый временной промежуток.");
        }
        List<ViewStatsDto> statDtoList;
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                statDtoList = statsRepository.getAllStatsDistinctIp(start, end);
            } else {
                statDtoList = statsRepository.getAllStats(start, end);
            }
        } else {
            if (unique) {
                statDtoList = statsRepository.getStatsByUrisDistinctIp(start, end, uris);
            } else {
                statDtoList = statsRepository.getStatsByUris(start, end, uris);
            }
        }
        log.info("StatsServiceImpl.getStats: {} - Finished", statDtoList);
        return statDtoList;
    }
}
