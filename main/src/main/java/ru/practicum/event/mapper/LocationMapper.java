package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    @Mapping(target = "latitude", source = "lat")
    @Mapping(target = "longitude", source = "lon")
    Location toLocation(LocationDto locationDto);
}
