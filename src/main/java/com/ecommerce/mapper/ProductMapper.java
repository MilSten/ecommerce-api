package com.ecommerce.mapper;

import com.ecommerce.dto.catalog.ProductDto;
import com.ecommerce.entity.catalog.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        CategoryMapper.class,
        ProductVariantMapper.class,
        ProductAttributeMapper.class,
        ProductImageMapper.class
})
public interface ProductMapper {

    @Mapping(target = "inStock", expression = "java(product.getStockQuantity() != null && product.getStockQuantity() > 0)")
    ProductDto toDto(Product product);

    Product toEntity(ProductDto productDto);
}