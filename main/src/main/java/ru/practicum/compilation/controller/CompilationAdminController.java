package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Validated
@Slf4j
public class CompilationAdminController {


    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("CompilationAdminController.create: {} - Started", newCompilationDto);
        CompilationDto compilationDto = compilationService.createCompilation(newCompilationDto);
        log.info("CompilationAdminController.create: {} - Finished", compilationDto);
        return compilationDto;
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto update(@PathVariable Long compId,
                                @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("CompilationAdminController.update: {}, {} - Started",compId, updateCompilationRequest);
        CompilationDto compilationDto = compilationService.updateCompilation(compId, updateCompilationRequest);
        log.info("CompilationAdminController.update: {} - Finished", compilationDto);
        return compilationDto;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long compId) {
        log.info("CompilationAdminController.update: {} - deleteById", compId);
        compilationService.deleteCompilationById(compId);
    }
}

