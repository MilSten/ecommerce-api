package com.ecommerce.mapper;

import com.ecommerce.dto.catalog.ProductAttributeDto;
import com.ecommerce.entity.catalog.ProductAttribute;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductAttributeMapper {
    ProductAttributeDto toDto(ProductAttribute productAttribute);

    ProductAttribute toEntity(ProductAttributeDto productAttributeDto);
}
