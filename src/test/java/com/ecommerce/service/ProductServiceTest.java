package com.ecommerce.service;

import com.ecommerce.dto.catalog.*;
import com.ecommerce.entity.MediaFile;
import com.ecommerce.entity.catalog.Category;
import com.ecommerce.entity.catalog.Product;
import com.ecommerce.entity.catalog.ProductVariant;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.MediaFileRepository;
import com.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MediaFileRepository mediaFileRepository;

    // ProductMapper — MapStruct-интерфейс. Мокируем его, чтобы не тянуть
    // весь граф зависимостей маппера в unit-тест.
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    // ─────────────────────────────────────────────────────────────────────────
    // Вспомогательные фабричные методы
    // Выносим создание тестовых объектов в методы, чтобы тесты оставались
    // читаемыми и не повторяли один и тот же код снова и снова.
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Минимально валидный DTO для создания товара (без вариантов, атрибутов, изображений).
     */
    private ProductCreateDto buildMinimalCreateDto(UUID categoryId) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(categoryId);

        ProductCreateDto dto = new ProductCreateDto();
        dto.setName("Test Product");
        dto.setSlug("test-product");
        dto.setPrice(BigDecimal.valueOf(99.99));
        dto.setStockQuantity(10);
        dto.setCategory(categoryDto);
        return dto;
    }

    /**
     * Сущность Category с нужным ID.
     */
    private Category buildCategory(UUID id) {
        Category category = new Category();
        category.setId(id);
        category.setName("Electronics");
        category.setSlug("electronics");
        category.setPosition(1);
        return category;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // createProduct
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("createProduct: категория не найдена → ResourceNotFoundException")
    void createProduct_categoryNotFound_throwsResourceNotFoundException() {
        UUID categoryId = UUID.randomUUID();
        ProductCreateDto dto = buildMinimalCreateDto(categoryId);

        // Репозиторий сигнализирует, что категории не существует
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(categoryId.toString());

        // Если категория не найдена — до сохранения товара доходить не должно
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("createProduct: изображение (MediaFile) не найдено → ResourceNotFoundException")
    void createProduct_imageNotFound_throwsResourceNotFoundException() {
        UUID categoryId = UUID.randomUUID();
        UUID imageId = UUID.randomUUID();

        ProductCreateDto dto = buildMinimalCreateDto(categoryId);

        // Добавляем ссылку на изображение, которого нет в репозитории
        ProductImageDto imageDto = new ProductImageDto();
        imageDto.setImageId(imageId);
        dto.setImages(List.of(imageDto));

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(buildCategory(categoryId)));
        when(mediaFileRepository.findById(imageId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(imageId.toString());
    }

    @Test
    @DisplayName("createProduct: варианты и атрибуты корректно добавляются к продукту")
    void createProduct_withVariantsAndAttributes_populatesCollections() {
        UUID categoryId = UUID.randomUUID();

        ProductCreateDto dto = buildMinimalCreateDto(categoryId);

        // Добавляем вариант товара (например, цвет + размер)
        ProductVariantDto variantDto = new ProductVariantDto();
        variantDto.setSku("SKU-001");
        variantDto.setName("Red / XL");
        variantDto.setPrice(BigDecimal.valueOf(109.99));
        variantDto.setStockQuantity(5);
        dto.setVariants(List.of(variantDto));

        // Добавляем атрибут (характеристику) товара
        ProductAttributeDto attrDto = new ProductAttributeDto();
        attrDto.setName("Color");
        attrDto.setValue("Red");
        dto.setAttributes(List.of(attrDto));

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(buildCategory(categoryId)));
        // thenAnswer возвращает первый аргумент вызова — тот самый product,
        // который пришёл в save(). Это позволяет захватить объект и проверить его состояние.
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(productMapper.toDto(any(Product.class))).thenReturn(new ProductDto());

        // Act
        productService.createProduct(dto);

        // ArgumentCaptor «захватывает» аргумент, с которым был вызван save(),
        // чтобы мы могли проверить поля объекта перед сохранением в БД.
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        Product savedProduct = captor.getValue();

        assertThat(savedProduct.getVariants()).hasSize(1);
        assertThat(savedProduct.getVariants().get(0).getSku()).isEqualTo("SKU-001");

        assertThat(savedProduct.getAttributes()).hasSize(1);
        assertThat(savedProduct.getAttributes().get(0).getName()).isEqualTo("Color");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // updateProduct
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateProduct: коллекции очищаются, saveAndFlush вызывается до save")
    void updateProduct_existingProduct_clearsOldCollectionsAndFlushesBeforeSave() {
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        // Исходный товар с устаревшим вариантом, который должен исчезнуть
        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setIsActive(true);

        ProductVariant oldVariant = new ProductVariant();
        oldVariant.setSku("OLD-SKU");
        existingProduct.getVariants().add(oldVariant);

        // DTO с новым вариантом, который должен заменить старый
        ProductCreateDto dto = buildMinimalCreateDto(categoryId);
        ProductVariantDto newVariantDto = new ProductVariantDto();
        newVariantDto.setSku("NEW-SKU");
        newVariantDto.setName("Blue / M");
        newVariantDto.setPrice(BigDecimal.valueOf(89.99));
        newVariantDto.setStockQuantity(3);
        dto.setVariants(List.of(newVariantDto));

        when(productRepository.findByIdWithRelations(productId)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(buildCategory(categoryId)));
        when(productRepository.saveAndFlush(existingProduct)).thenReturn(existingProduct);
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);
        when(productMapper.toDto(any())).thenReturn(new ProductDto());

        // Act
        productService.updateProduct(productId, dto);

        // Ключевая проверка порядка вызовов:
        // saveAndFlush должен предшествовать save — иначе Hibernate вставит
        // новые варианты (NEW-SKU) раньше, чем удалит OLD-SKU, и сломает
        // уникальный индекс на колонке sku.
        var inOrder = inOrder(productRepository);
        inOrder.verify(productRepository).saveAndFlush(existingProduct);
        inOrder.verify(productRepository).save(existingProduct);

        // После обновления — только новый вариант, старый удалён через clear()
        assertThat(existingProduct.getVariants()).hasSize(1);
        assertThat(existingProduct.getVariants().get(0).getSku()).isEqualTo("NEW-SKU");
    }

    @Test
    @DisplayName("updateProduct: товар не найден → ResourceNotFoundException")
    void updateProduct_productNotFound_throwsResourceNotFoundException() {
        UUID productId = UUID.randomUUID();
        when(productRepository.findByIdWithRelations(productId)).thenReturn(Optional.empty());

        ProductCreateDto dto = buildMinimalCreateDto(UUID.randomUUID());

        assertThatThrownBy(() -> productService.updateProduct(productId, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(productId.toString());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // deleteProduct (soft delete)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteProduct: товар не удаляется физически — только isActive → false")
    void deleteProduct_existingProduct_setsIsActiveFalseWithoutPhysicalDelete() {
        UUID productId = UUID.randomUUID();

        Product product = new Product();
        product.setId(productId);
        product.setIsActive(true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        // Act
        productService.deleteProduct(productId);

        // Товар помечен неактивным — «мягкое удаление» (soft delete)
        assertThat(product.getIsActive()).isFalse();

        // save() — вызывается для записи изменений
        verify(productRepository).save(product);

        // delete() — НЕ должен вызываться, запись остаётся в БД
        verify(productRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deleteProduct: товар не найден → ResourceNotFoundException")
    void deleteProduct_notFound_throwsResourceNotFoundException() {
        UUID productId = UUID.randomUUID();
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(productId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(productId.toString());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // getProductById
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getProductById: товар не найден → ResourceNotFoundException")
    void getProductById_notFound_throwsResourceNotFoundException() {
        UUID productId = UUID.randomUUID();
        when(productRepository.findByIdWithRelations(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(productId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(productId.toString());
    }
}
