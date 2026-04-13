package com.ecommerce.dto.catalog;

import com.ecommerce.dto.BaseDto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductAttributeDto extends BaseDto {

    @NotBlank(message = "Attribute name is required")
    private String name;

    @NotBlank(message = "Attribute value is required")
    private String value;
}
