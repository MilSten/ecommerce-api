package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.dto.catalog.ProductCreateDto;
import com.ecommerce.dto.catalog.ProductDto;
import com.ecommerce.dto.catalog.ProductFilterDto;
import com.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Endpoints for product management")
public class ProductController {

    private final ProductService productService;

    /**
     * GET /api/v1/products
     */
    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve products with optional filtering")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) java.math.BigDecimal minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "newest") String sort
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));

        ProductFilterDto filter = new ProductFilterDto();
        filter.setSearch(search);
        filter.setCategoryId(categoryId);
        filter.setMinPrice(minPrice);
        filter.setMaxPrice(maxPrice);
        filter.setMinRating(minRating);
        filter.setSort(sort);

        var products = productService.getProducts(filter, pageable);
        return ResponseEntity.ok(new ApiResponse<>(products, HttpStatus.OK.value()));
    }

    /**
     * GET /api/v1/products/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get products by ID", description = "Retrieve a product by its unique ID")
    public ResponseEntity<ApiResponse<ProductDto>> getProduct(@PathVariable UUID id) {
        var product = productService.getProductById(id);
        return ResponseEntity.ok(new ApiResponse<>(product, HttpStatus.OK.value()));
    }

    /**
     * GET /api/v1/products/slug/{slug}
     */
    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get products by slug", description = "Retrieve a product by its unique slug")
    public ResponseEntity<ApiResponse<ProductDto>> getProductBySlug(@PathVariable String slug) {
        var product = productService.getProductBySlug(slug);
        return ResponseEntity.ok(new ApiResponse<>(product, HttpStatus.OK.value()));
    }

    /**
     * POST /api/v1/admin/products (только ADMIN/MANAGER)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new product", description = "Create a new product (Admin/Manager only)")
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(
            @Valid @RequestBody ProductCreateDto dto
    ) {
        var product = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(product, HttpStatus.CREATED.value(), "Product created successfully"));
    }

    /**
     * PUT /api/v1/admin/products/{id} (только ADMIN/MANAGER)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update an existing product", description = "Update an existing product by ID (Admin/Manager only)")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductCreateDto dto
    ) {
        var product = productService.updateProduct(id, dto);
        return ResponseEntity.ok(new ApiResponse<>(product, HttpStatus.OK.value(), "Product updated successfully"));
    }

    /**
     * DELETE /api/v1/admin/products/{id} (только ADMIN/MANAGER)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete a product", description = "Delete a product by ID (Admin/Manager only)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(new ApiResponse<>(null, HttpStatus.OK.value(), "Product deleted successfully"));
    }
}