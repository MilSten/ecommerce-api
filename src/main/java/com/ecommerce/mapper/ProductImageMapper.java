package com.ecommerce.mapper;

import com.ecommerce.dto.catalog.ProductImageDto;
import com.ecommerce.entity.catalog.ProductImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    ProductImageDto toDto(ProductImage productImage);

    ProductImage toEntity(ProductImageDto productImageDto);
}
