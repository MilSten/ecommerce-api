package com.ecommerce.mapper;

import com.ecommerce.dto.catalog.CategoryDto;
import com.ecommerce.entity.catalog.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);
    Category toEntity(CategoryDto categoryDto);
}
