package com.ecommerce.mapper;

import com.ecommerce.dto.catalog.ProductAttributeDto;
import com.ecommerce.entity.catalog.ProductAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductAttributeMapper {

    ProductAttributeDto toDto(ProductAttribute productAttribute);

    @Mapping(target = "product", ignore = true)
    ProductAttribute toEntity(ProductAttributeDto productAttributeDto);
}
