package com.ecommerce.mapper;

import com.ecommerce.dto.catalog.ProductDto;
import com.ecommerce.entity.catalog.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {
        CategoryMapper.class,
        ProductVariantMapper.class,
        ProductAttributeMapper.class,
        ProductImageMapper.class
})
public interface ProductMapper {
    ProductDto toDto(Product product);

    Product toEntity(ProductDto productDto);
}