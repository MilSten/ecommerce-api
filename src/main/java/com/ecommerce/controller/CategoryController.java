package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.catalog.CategoryDto;
import com.ecommerce.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Endpoints for category management")
class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieve all categories")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> findAll() {
        var category = categoryService.getAllCategories();
        return ResponseEntity.ok(new ApiResponse<>(category, HttpStatus.OK.value()));
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active categories", description = "Retrieve all active categories")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> findAllActive() {
        var category = categoryService.getAllActiveCategories();
        return ResponseEntity.ok(new ApiResponse<>(category, HttpStatus.OK.value()));
    }

    @GetMapping("/{id}/children")
    @Operation(summary = "Get all children categories for category id", description = "Retrieve all children categories for category id")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> findAllChildrenForCategoryId(@PathVariable UUID id) {
        var category = categoryService.getAllChildrenCategoriesForCategory(id);
        return ResponseEntity.ok(new ApiResponse<>(category, HttpStatus.OK.value()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by id", description = "Retrieve category by id")
    public ResponseEntity<ApiResponse<CategoryDto>> findCategoryById(@PathVariable UUID id) {
        var category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(new ApiResponse<>(category, HttpStatus.OK.value()));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get category by slug", description = "Retrieve category by slug")
    public ResponseEntity<ApiResponse<CategoryDto>> findCategoryBySlug(@PathVariable String slug) {
        var category = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(new ApiResponse<>(category, HttpStatus.OK.value()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create category", description = "Create a new category")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(
            @Valid @RequestBody CategoryDto dto) {
        var category = categoryService.createCategory(dto);
        return ResponseEntity.ok(new ApiResponse<>(category, HttpStatus.OK.value()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update category", description = "Update an existing category by id")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryDto categoryDto
    ) {
        var category = categoryService.updateCategory(id, categoryDto);
        return ResponseEntity.ok(new ApiResponse<>(category, HttpStatus.OK.value()));
    }

     @DeleteMapping("/{id}")
     @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
     @Operation(summary = "Delete category", description = "Delete a category by id")
     public ResponseEntity<ApiResponse<Void>> deleteCategoryById(@PathVariable UUID id) {
         categoryService.deleteCategoryById(id);
         return ResponseEntity.ok(new ApiResponse<>(null, HttpStatus.OK.value()));
     }

    @DeleteMapping("")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete list categories", description = "Delete a list categories by id")
    public ResponseEntity<ApiResponse<Void>> deleteCategoriesByIds(@RequestParam List<UUID> ids) {
        categoryService.deleteCategoriesByIds(ids);
        return ResponseEntity.ok(new ApiResponse<>(null, HttpStatus.OK.value()));
    }

}
