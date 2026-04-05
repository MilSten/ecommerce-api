package com.ecommerce.service;

import com.ecommerce.dto.catalog.ProductCreateDto;
import com.ecommerce.dto.catalog.ProductDto;
import com.ecommerce.dto.catalog.ProductFilterDto;
import com.ecommerce.entity.catalog.Product;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    /**
     * Получить товар по ID
     */
    @Transactional(readOnly = true)
    public ProductDto getProductById(UUID id) {
        log.debug("Fetching product with id: {}", id);
        return productRepository.findByIdWithRelations(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    /**
     * Получить товар по slug
     */
    @Transactional(readOnly = true)
    public ProductDto getProductBySlug(String slug) {
        log.debug("Fetching product with slug: {}", slug);
        return productRepository.findBySlug(slug)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with slug: " + slug));
    }

    /**
     * Получить все товары с фильтрацией
     */
    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(ProductFilterDto filter, Pageable pageable) {
        log.debug("Fetching products with filter: {}", filter);

        return productRepository.findByFilters(
                filter.getSearch(),
                filter.getCategoryId(),
                filter.getMinPrice(),
                filter.getMaxPrice(),
                filter.getMinRating(),
                pageable
        ).map(productMapper::toDto);
    }

    /**
     * Создать товар (только ADMIN/MANAGER)
     */
    public ProductDto createProduct(ProductCreateDto dto) {
        log.info("Creating new product: {}", dto.getName());

        var category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));

        Product product = new Product();
        product.setName(dto.getName());
        product.setSlug(dto.getSlug());
        product.setDescription(dto.getDescription());
        product.setShortDescription(dto.getShortDescription());
        product.setPrice(dto.getPrice());
        product.setCost(dto.getCost());
        product.setStockQuantity(dto.getStockQuantity());
        product.setCategory(category);
        product.setIsActive(true);

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with id: {}", savedProduct.getId());

        return productMapper.toDto(savedProduct);
    }

    /**
     * Обновить товар (только ADMIN/MANAGER)
     */
    public ProductDto updateProduct(UUID id, ProductCreateDto dto) {
        log.info("Updating product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        var category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));

        product.setName(dto.getName());
        product.setSlug(dto.getSlug());
        product.setDescription(dto.getDescription());
        product.setShortDescription(dto.getShortDescription());
        product.setPrice(dto.getPrice());
        product.setCost(dto.getCost());
        product.setStockQuantity(dto.getStockQuantity());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with id: {}", id);

        return productMapper.toDto(updatedProduct);
    }

    /**
     * Удалить товар (мягкое удаление - soft delete)
     */
    public void deleteProduct(UUID id) {
        log.info("Deleting product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setIsActive(false);
        productRepository.save(product);

        log.info("Product deleted successfully with id: {}", id);
    }
}