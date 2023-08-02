package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;


@Mapper(componentModel = "spring", uses = {UserMapper.class, CategoryMapper.class, LocationMapper.class})
public interface EventMapper {
    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "publishedOn", expression = "java(null)")
    Event toEvent(NewEventDto newEventDto,
                  User initiator,
                  Category category,
                  Location location,
                  LocalDateTime createdOn,
                  EventState state);

    EventFullDto toEventFullDto(Event event, Long views);

    EventShortDto toEventShortDto(Event event, Long views);
}
