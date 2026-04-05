package com.ecommerce.dto.catalog;

import com.ecommerce.dto.BaseDto;
import com.ecommerce.entity.catalog.Category;
import com.ecommerce.entity.catalog.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CategoryDto extends BaseDto {
    private String name;
    private String slug;
    private String description;
    private Category parentCategory;
    private Boolean isActive = true;
    private Integer position = 0;
    private List<Category> subCategories = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
}
