package ru.practicum.mapper;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.Stats;


public class StatsMapper {

    public static Stats toStats(EndpointHitDto endpointHitDto) {
        Stats stats = new Stats();
            stats.setApp(endpointHitDto.getApp());
            stats.setUri(endpointHitDto.getUri());
            stats.setIp(endpointHitDto.getIp());
            stats.setTimestamp(endpointHitDto.getTimestamp());
            if (endpointHitDto.getId() != null) {
                stats.setId(endpointHitDto.getId());
            }
        return stats;
    }

    public static EndpointHitDto toEndpointHitDto(Stats stats) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
            endpointHitDto.setId(stats.getId());
            endpointHitDto.setApp(stats.getApp());
            endpointHitDto.setUri(stats.getUri());
            endpointHitDto.setIp(stats.getIp());
            endpointHitDto.setTimestamp(stats.getTimestamp());

        return endpointHitDto;
    }
}
