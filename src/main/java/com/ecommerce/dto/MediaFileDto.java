package com.ecommerce.dto;

import com.ecommerce.dto.BaseDto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MediaFileDto extends BaseDto {
    private String originalFilename;
    private String filename;
    private String filePath;
    private Long fileSize;
    private String mimeType;
}
