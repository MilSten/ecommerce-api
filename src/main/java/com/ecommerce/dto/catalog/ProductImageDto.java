package com.ecommerce.dto.catalog;

import com.ecommerce.dto.BaseDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductImageDto extends BaseDto {

    // При создании — ID загруженного MediaFile; при чтении — заполняется маппером
    @NotNull(message = "Image ID is required")
    private UUID imageId;

    private String filePath;
    private String originalFilename;
    private String position;
    private boolean isMain;
}
