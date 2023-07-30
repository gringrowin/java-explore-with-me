package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
@Validated
@Slf4j
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("CategoryAdminController.create: {},  - Started", newCategoryDto);
        CategoryDto categoryDto = categoryService.create(newCategoryDto);
        log.info("CategoryAdminController.create: {},  - Finished", categoryDto);
        return categoryDto;
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto patch(@PathVariable Long catId,
                             @Valid @RequestBody CategoryDto categoryDto) {
        log.info("CategoryAdminController.patch: {}, {}  - Started", catId, categoryDto);
        CategoryDto updatedCategoryDto = categoryService.update(catId, categoryDto);
        log.info("CategoryAdminController.patch: {},  - Finished", updatedCategoryDto);
        return updatedCategoryDto;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long catId) {
        log.info("CategoryAdminController.deleteById: {},  - Started", catId);
        categoryService.deleteCategory(catId);
    }
}
