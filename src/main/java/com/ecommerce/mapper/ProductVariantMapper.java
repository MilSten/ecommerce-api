package com.ecommerce.mapper;

import com.ecommerce.dto.catalog.ProductVariantDto;
import com.ecommerce.entity.catalog.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {

    ProductVariantDto toDto(ProductVariant productVariant);

    @Mapping(target = "product", ignore = true)
    ProductVariant toEntity(ProductVariantDto productVariantDto);
}
