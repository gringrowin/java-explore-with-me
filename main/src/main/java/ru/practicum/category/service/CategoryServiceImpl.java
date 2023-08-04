package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        log.info("CategoryServiceImpl.create: {} - Started", newCategoryDto);
        Category category = categoryRepository.save(categoryMapper.toCategory(newCategoryDto));
        CategoryDto categoryDto = categoryMapper.toCategoryDto(category);
        log.info("CategoryServiceImpl.create: {} - Finished", categoryDto);
        return categoryDto;
    }

    @Override
    @Transactional
    public CategoryDto update(Long catId, CategoryDto categoryDto) {
        log.info("CategoryServiceImpl.update: {}, {} - Started", catId, categoryDto);
        Category category = findCategoryById(catId);
        categoryDto.setId(category.getId());
        category = categoryRepository.save(categoryMapper.toCategory(categoryDto));
        CategoryDto updatedCategoryDto = categoryMapper.toCategoryDto(category);
        log.info("CategoryServiceImpl.update: {} - Finished", updatedCategoryDto);
        return updatedCategoryDto;
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        log.info("CategoryServiceImpl.deleteCategory: {} - Started", catId);
        findCategoryById(catId);
        categoryRepository.deleteById(catId);
        log.info("CategoryServiceImpl.deleteCategory: {} - Finished", catId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAll(Integer from, Integer size) {
        log.info("CategoryServiceImpl.getAll: {}, {} - Started", from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        List<CategoryDto> categoryDtoList = categoryRepository.findAll(pageable)
                .stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
        log.info("CategoryServiceImpl.getAll: {} - Finished", categoryDtoList);
        return categoryDtoList;
    }

    @Override
    public CategoryDto getById(Long catId) {
        log.info("CategoryServiceImpl.getById: {} - Started", catId);
        Category category = findCategoryById(catId);
        CategoryDto categoryDto = categoryMapper.toCategoryDto(category);
        log.info("CategoryServiceImpl.getById: {} - Finished", categoryDto);
        return categoryDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Category findCategoryById(Long catId) {
        log.info("CategoryServiceImpl.findCategoryById: {} ", catId);
        return  categoryRepository.findById(catId)
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format("Категория с ID : %s не найдена", catId))
                );
    }


}
