package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.Stats;

@Mapper(componentModel = "spring")
public interface StatsMapper {

    Stats toStats(EndpointHitDto endpointHitDto);

    EndpointHitDto toEndpointHitDto(Stats stats);

}
