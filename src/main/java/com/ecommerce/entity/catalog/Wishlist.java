package com.ecommerce.entity.catalog;

import com.ecommerce.entity.BaseEntity;
import com.ecommerce.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wishlists", indexes = {
        @Index(name = "idx_wishlist_user", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Wishlist extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
