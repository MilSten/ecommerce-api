package com.ecommerce.dto.catalog;

import com.ecommerce.dto.BaseDto;
import com.ecommerce.entity.catalog.Product;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductVariantDto extends BaseDto {
    private Product product;
    private String sku;
    private String name;
    private BigDecimal price = BigDecimal.ZERO;
    private Integer stockQuantity = 0;
}
