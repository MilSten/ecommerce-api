package com.ecommerce.mapper;

import com.ecommerce.dto.catalog.ProductImageDto;
import com.ecommerce.entity.catalog.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

    @Mapping(source = "image.id", target = "imageId")
    @Mapping(source = "image.filePath", target = "filePath")
    @Mapping(source = "image.originalFilename", target = "originalFilename")
    ProductImageDto toDto(ProductImage productImage);

    @Mapping(target = "image", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductImage toEntity(ProductImageDto productImageDto);
}
