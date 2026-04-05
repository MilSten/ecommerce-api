package com.ecommerce.entity.coupon;

import com.ecommerce.entity.BaseEntity;
import com.ecommerce.entity.order.Order;
import com.ecommerce.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_usages", indexes = {
        @Index(name = "idx_coupon_usage_coupon_id", columnList = "coupon_id"),
        @Index(name = "idx_coupon_usage_user_id", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CouponUsage extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    private LocalDateTime usedAt;
}
