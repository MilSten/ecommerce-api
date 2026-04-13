package com.ecommerce.dto.catalog;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDto {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Slug is required")
    private String slug;

    private String description;
    private String shortDescription;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    private BigDecimal cost;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be non-negative")
    private Integer stockQuantity;

    private Boolean isActive = true;

    // Игнорируется — определяется из stockQuantity на стороне сервера
    private Boolean inStock;

    @NotNull(message = "Category is required")
    private CategoryDto category;

    @Valid
    private List<ProductAttributeDto> attributes = new ArrayList<>();

    @Valid
    private List<ProductVariantDto> variants = new ArrayList<>();

    @Valid
    private List<ProductImageDto> images = new ArrayList<>();
}
