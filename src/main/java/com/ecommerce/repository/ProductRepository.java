package com.ecommerce.repository;

import com.ecommerce.entity.catalog.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends BaseRepository<Product> {

    Optional<Product> findBySlug(String slug);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
            "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:minRating IS NULL OR p.rating >= :minRating)")
    Page<Product> findByFilters(
            @Param("search") String search,
            @Param("categoryId") UUID categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minRating") BigDecimal minRating,
            Pageable pageable
    );

    @Query(value = "SELECT p FROM Product p LEFT JOIN FETCH p.variants v LEFT JOIN FETCH p.images img LEFT JOIN FETCH p.attributes WHERE p.id = :id")
    Optional<Product> findByIdWithRelations(@Param("id") UUID id);
}