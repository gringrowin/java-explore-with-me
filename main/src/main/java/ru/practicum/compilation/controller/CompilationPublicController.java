package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
@Validated
@Slf4j
public class CompilationPublicController {

    private final CompilationService compilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getAll(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("CompilationPublicController.getAll: {}, {}, {} - Started", pinned, from, size);
        List<CompilationDto> compilationDtoList = compilationService.getAllCompilations(pinned, from, size);
        log.info("CompilationPublicController.getAll: {} - Finished", compilationDtoList.size());
        return compilationDtoList;
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getById(@PathVariable Long compId) {
        log.info("CompilationPublicController.getById: {} - Started", compId);
        CompilationDto compilationDto = compilationService.getCompilationById(compId);
        log.info("CompilationPublicController.getById: {} - Finished", compilationDto);
        return compilationDto;
    }
}
