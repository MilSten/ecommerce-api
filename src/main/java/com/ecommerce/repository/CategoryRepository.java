package com.ecommerce.repository;

import com.ecommerce.entity.catalog.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends BaseRepository<Category> {

    Optional<Category> findBySlug(String slug);

    List<Category> findByIsActiveTrue();

    List<Category> findByParentCategoryId(UUID parentCategoryId);
}