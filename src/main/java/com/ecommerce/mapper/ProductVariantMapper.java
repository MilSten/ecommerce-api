package com.ecommerce.mapper;

import com.ecommerce.dto.catalog.ProductVariantDto;
import com.ecommerce.entity.catalog.ProductVariant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {
    ProductVariantDto toDto(ProductVariant productVariant);

    ProductVariant toEntity(ProductVariantDto productVariantDto);
}
