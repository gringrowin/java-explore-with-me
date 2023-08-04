package ru.practicum.category.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto update(Long catId, CategoryDto categoryDto);

    void deleteCategory(Long catId);

    CategoryDto getById(Long catId);

    @Transactional(readOnly = true)
    Category findCategoryById(Long userId);

    List<CategoryDto> getAll(Integer from, Integer size);
}
