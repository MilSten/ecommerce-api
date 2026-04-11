package com.ecommerce.mapper;

import com.ecommerce.dto.catalog.CategoryDto;
import com.ecommerce.entity.catalog.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "parentCategory", expression = "java(toId(category))")
    CategoryDto toDto(Category category);

    @Mapping(target = "parentCategory", ignore = true)
    Category toEntity(CategoryDto categoryDto);

    default UUID toId(Category category) {
        return category.getParentCategory() != null ? category.getParentCategory().getId() : null;
    }
}
