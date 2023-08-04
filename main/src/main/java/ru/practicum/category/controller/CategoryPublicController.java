package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Validated
@Slf4j
public class CategoryPublicController {

    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getAll(
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("CategoryPublicController.getAll: {}, {} - Started", from, size);
        List<CategoryDto> categoryDtoList = categoryService.getAll(from, size);
        log.info("CategoryPublicController.getAll: {} - Finished", categoryDtoList);
        return categoryDtoList;
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getById(@PathVariable Long catId) {
        log.info("CategoryPublicController.getById: {},  - Started", catId);
        CategoryDto categoryDto = categoryService.getById(catId);
        log.info("CategoryPublicController.getById: {},  - Finished", categoryDto);
        return categoryDto;
    }
}
