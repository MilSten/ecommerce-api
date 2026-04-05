package com.ecommerce.dto.catalog;

import com.ecommerce.dto.BaseDto;
import com.ecommerce.entity.catalog.Product;
import com.ecommerce.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WishlistDto extends BaseDto {
    private User user;
    private Product product;
}
