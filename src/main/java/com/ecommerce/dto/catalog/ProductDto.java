package com.ecommerce.dto.catalog;

import com.ecommerce.dto.BaseDto;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductDto extends BaseDto {
    private String name;
    private String slug;
    private String description;
    private String shortDescription;
    private BigDecimal price;
    private BigDecimal cost;
    private Boolean isActive;
    private Integer stockQuantity;
    private boolean inStock;
    private BigDecimal rating;
    private Integer reviewCount;

    private CategoryDto category;
    private List<ProductVariantDto> variants;
    private List<ProductAttributeDto> attributes;
    private List<ProductImageDto> images;
}