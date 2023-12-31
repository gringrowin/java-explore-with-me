package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface StatsRepository extends JpaRepository<Stats, Long> {
    @Query("SELECT new ru.practicum.dto.ViewStatsDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stats AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStatsDto> getAllStatsDistinctIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stats AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStatsDto> getAllStats(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stats AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "AND s.uri IN ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStatsDto> getStatsByUrisDistinctIp(LocalDateTime start, LocalDateTime end, Set<String> uri);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stats AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "AND s.uri IN ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStatsDto> getStatsByUris(LocalDateTime start, LocalDateTime end, Set<String> uri);
}
