package com.ecommerce.service;

import com.ecommerce.dto.catalog.CategoryDto;
import com.ecommerce.entity.catalog.Category;
import com.ecommerce.mapper.CategoryMapper;
import com.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Получить все категории.
     */
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        log.info("Fetching all categories");

        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Получить все активные категории
     */
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllActiveCategories() {
        log.info("Fetching all active categories");

        return categoryRepository.findByIsActiveTrue().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Получить все дочерние категории для родительской (без родителя).
     */
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllChildrenCategoriesForCategory(UUID id) {
        log.info("Fetching all children categories for category {}", id);
        List<CategoryDto> childrenCategory = categoryRepository.findByParentCategoryId(id).stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} children categories for category {}", childrenCategory.size(), id);

        return childrenCategory;
    }

    /**
    * Получить категорию по ее ID.
    */
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(UUID id) {
        log.info("Fetching category with id: {}", id);

        return categoryMapper.toDto(categoryRepository.findById(id).orElse(null));
    }

    /**
     * Получить категорию по ее slug.
     */
    @Transactional(readOnly = true)
    public CategoryDto getCategoryBySlug(String slug) {
        log.info("Fetching category with slug: {}", slug);

        return categoryRepository.findBySlug(slug)
                .map(categoryMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Category not found with slug: " + slug));
    }

    /**
     * Создать новую категорию.
     */
    public CategoryDto createCategory(CategoryDto categoryDto) {
        log.info("Creating new category with name: {}", categoryDto.getName());

        var category = categoryMapper.toEntity(categoryDto);

        if (categoryDto.getParentCategory() != null) {
            var parentCategory = categoryRepository.findById(categoryDto.getParentCategory())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + categoryDto.getParentCategory()));
            category.setParentCategory(parentCategory);
        } else {
            category.setParentCategory(null);
        }
        var savedCategory = categoryRepository.save(category);

        log.debug("Created category with name: {}", category.getName());

        return categoryMapper.toDto(savedCategory);
    }

    /**
     * Удалить категорию (жесткое удаление)
     */
    public void deleteCategoryById(UUID id) {
        log.info("Deleting category with id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        categoryRepository.delete(category);

        log.info("Deleted Category with id: {}", id);
    }

    /**
     * Удалить список категорий (жесткое удаление)
     */
    public void deleteCategoriesByIds(List<UUID> ids) {
        log.info("Deleting categories with ids: {}", ids);

        List<Category> categories = categoryRepository.findAllById(ids);
        if (categories.size() != ids.size()) {
            List<UUID> foundIds = categories.stream()
                    .map(Category::getId)
                    .toList();
            List<UUID> notFoundIds = ids.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new RuntimeException("Categories not found with ids: " + notFoundIds);
        }

        categoryRepository.deleteAll(categories);

        log.info("Deleted categories with ids: {}", ids);
    }

    /**
     * Обновить категорию (только ADMIN/MANAGER)
     */
    public CategoryDto updateCategory(UUID id, CategoryDto categoryDto) {
        log.info("Updating category with id: {}", id);

        var existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        existingCategory.setName(categoryDto.getName());
        existingCategory.setSlug(categoryDto.getSlug());
        existingCategory.setDescription(categoryDto.getDescription());
        existingCategory.setIsActive(categoryDto.getIsActive());
        existingCategory.setPosition(categoryDto.getPosition());

        if (categoryDto.getParentCategory() != null) {
            var parentCategory = categoryRepository.findById(categoryDto.getParentCategory())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + categoryDto.getParentCategory()));
            existingCategory.setParentCategory(parentCategory);
        } else {
            existingCategory.setParentCategory(null);
        }

        var updatedCategory = categoryRepository.save(existingCategory);

        log.info("Updated category with id: {}", id);

        return categoryMapper.toDto(updatedCategory);
    }
}
