package com.ecommerce.dto.catalog;

import com.ecommerce.dto.BaseDto;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductVariantDto extends BaseDto {

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Variant name is required")
    private String name;

    @NotNull(message = "Variant price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Variant price must be greater than 0")
    private BigDecimal price = BigDecimal.ZERO;

    @NotNull
    @Min(value = 0, message = "Stock quantity must be non-negative")
    private Integer stockQuantity = 0;
}
