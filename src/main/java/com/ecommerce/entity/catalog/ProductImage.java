package com.ecommerce.entity.catalog;

import com.ecommerce.entity.BaseEntity;
import com.ecommerce.entity.MediaFile;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_images", indexes = {
        @Index(name = "idx_product_image_product", columnList = "product_id"),
        @Index(name = "idx_product_image_image", columnList = "image_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductImage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private MediaFile image;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private boolean isMain;
}