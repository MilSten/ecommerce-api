package com.ecommerce.dto.catalog;

import com.ecommerce.dto.BaseDto;
import com.ecommerce.entity.catalog.Product;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductAttributeDto extends BaseDto {
    private Product product;
    private String name;
    private String value;
}
