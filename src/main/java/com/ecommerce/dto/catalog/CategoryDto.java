package com.ecommerce.dto.catalog;

import com.ecommerce.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CategoryDto extends BaseDto {
    private String name;
    private String slug;
    private String description;
    private UUID parentCategory;
    private Boolean isActive = true;
    private Integer position = 0;
}
