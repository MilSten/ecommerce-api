package com.ecommerce.entity.order;

import com.ecommerce.entity.BaseEntity;
import com.ecommerce.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "carts", indexes = {
        @Index(name = "idx_cart_user", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Cart extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String session;
}
