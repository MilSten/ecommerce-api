package com.ecommerce.entity.catalog;

import com.ecommerce.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories", indexes = {
        @Index(name = "idx_category_slug", columnList = "slug")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Integer position = 0;

    @OneToMany(mappedBy = "parentCategory")
    private java.util.List<Category> subCategories = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "category")
    private java.util.List<Product> products = new java.util.ArrayList<>();
}