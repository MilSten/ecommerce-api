package com.ecommerce.dto.catalog;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterDto {
    private UUID categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal minRating;
    private String search;
    private Integer page = 0;
    private Integer size = 20;
    private String sort = "newest"; // price_asc, price_desc, rating, newest
}