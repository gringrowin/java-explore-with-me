package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventService eventService;

    private final CompilationMapper compilationMapper;

    @Override
    @Transactional
    public CompilationDto createCompilation (NewCompilationDto newCompilationDto) {
        log.info("CompilationServiceImpl.createCompilation: {} - Started", newCompilationDto);

        List<Event> events = new ArrayList<>();

        if (!newCompilationDto.getEvents().isEmpty()) {
            events = eventService.getEventsByIds(newCompilationDto.getEvents());
            if (events.size() != newCompilationDto.getEvents().size()) {
                throw new NotFoundException("Часть событий не найдены.");
            }
        }

        Compilation compilation = compilationRepository.save(
                compilationMapper.toCompilation(newCompilationDto, events));

        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilation);
        log.info("CompilationServiceImpl.createCompilation: {} - Finished", compilationDto);
        return compilationDto;
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("CompilationServiceImpl.updateCompilation: {}, {} - Started", compId, updateCompilationRequest);

        Compilation compilation = findCompilationById(compId);
        List<Event> events = new ArrayList<>();

        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }

        if (updateCompilationRequest.getEvents() != null) {
            events = eventService.getEventsByIds(updateCompilationRequest.getEvents());
            if (events.size() != updateCompilationRequest.getEvents().size()) {
                throw new NotFoundException("Часть событий не найдены.");
            }
            compilation.setEvents(events);
        }
        compilation = compilationRepository.save(compilation);
        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilation);
        log.info("CompilationServiceImpl.updateCompilation: {} - Finished", compilationDto);
        return compilationDto;
    }

    @Override
    public void deleteCompilationById(Long compId) {
        log.info("CompilationServiceImpl.deleteCompilationById: {} - Started", compId);
        findCompilationById(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("CompilationPublicController.getAllCompilations: {}, {}, {} - Started", pinned, from, size);
        List<CompilationDto> compilationDtos = new ArrayList<>();
        if (pinned != null) {
            List<Compilation> compilations =
                    compilationRepository.findByPinned(pinned, PageRequest.of(from / size, size));
            compilations
                    .forEach(compilation -> compilationDtos.add(compilationMapper.toCompilationDto(compilation)));
        } else {
            compilationRepository.findAll(PageRequest.of(from / size, size))
                    .forEach(compilation -> compilationDtos.add(compilationMapper.toCompilationDto(compilation)));
        }

        log.info("CompilationPublicController.getAllCompilations: {} - Finished", compilationDtos);
        return compilationDtos;
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        log.info("CompilationPublicController.getCompilationById: {} - Started", compId);
        CompilationDto compilationDto = compilationMapper.toCompilationDto(findCompilationById(compId));
        log.info("CompilationPublicController.getCompilationById: {} - Finished", compilationDto);
        return compilationDto;
    }

    @Override
    public Compilation findCompilationById(Long compId) {
        log.info("CompilationServiceImpl.findCompilationById: {} ", compId);
        return compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(
                        String.format("Подборки с ID : %s не найдено", compId))
        );
    }
}
