package com.ecommerce.dto.catalog;

import com.ecommerce.dto.BaseDto;
import com.ecommerce.entity.MediaFile;
import com.ecommerce.entity.catalog.Product;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductImageDto extends BaseDto {
    private Product product;
    private MediaFile image;
    private String position;
    private boolean isMain;
}
