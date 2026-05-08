package com.nikos.freshstore.catalog.service.impl;

import com.nikos.freshstore.catalog.domain.Category;
import com.nikos.freshstore.catalog.domain.Product;
import com.nikos.freshstore.catalog.domain.ProductAttribute;
import com.nikos.freshstore.catalog.domain.ProductImage;
import com.nikos.freshstore.catalog.dto.*;
import com.nikos.freshstore.catalog.exception.DuplicateResourceException;
import com.nikos.freshstore.catalog.exception.ResourceNotFoundException;
import com.nikos.freshstore.catalog.mapper.ProductMapper;
import com.nikos.freshstore.catalog.repository.CategoryRepository;
import com.nikos.freshstore.catalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    // ── Shared test fixtures ──────────────────────────────────────────────────

    private UUID productId;
    private UUID categoryId;
    private Category category;
    private Product product;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        productId  = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        category = new Category();
        category.setId(categoryId);
        category.setName("Plant-based Milk");
        category.setSlug("plant-based-milk");

        product = new Product();
        product.setId(productId);
        product.setName("Oat Milk");
        product.setSlug("oat-milk");
        product.setBarcode("5000000003");
        product.setUnitType("litre");
        product.setUnitSize(1.0f);
        product.setCategory(category);
        product.setActive(true);

        // add a primary image
        ProductImage primaryImage = new ProductImage();
        primaryImage.setId(UUID.randomUUID());
        primaryImage.setUrl("/images/oat-milk-1.jpg");
        primaryImage.setAltText("Oat milk carton front");
        primaryImage.setPrimary(true);
        primaryImage.setSortOrder(1);
        product.getImages().add(primaryImage);

        // add an attribute
        ProductAttribute attribute = new ProductAttribute();
        attribute.setId(UUID.randomUUID());
        attribute.setAttrKey("vegan");
        attribute.setAttrValue("true");
        product.getAttributes().add(attribute);

        // build a valid ProductRequest
        ProductImageRequest imageRequest = new ProductImageRequest();
        imageRequest.setUrl("/images/oat-milk-1.jpg");
        imageRequest.setAltText("Oat milk carton front");
        imageRequest.setPrimary(true);
        imageRequest.setSortOrder(1);

        ProductAttributeRequest attributeRequest = new ProductAttributeRequest();
        attributeRequest.setAttrKey("vegan");
        attributeRequest.setAttrValue("true");

        productRequest = new ProductRequest();
        productRequest.setName("Oat Milk");
        productRequest.setDescription("Barista oat drink, 1 litre.");
        productRequest.setCategoryId(categoryId);
        productRequest.setBrand("OatlyDay");
        productRequest.setBarcode("5000000003");
        productRequest.setUnitType("litre");
        productRequest.setUnitSize(1.0f);
        productRequest.setImages(List.of(imageRequest));
        productRequest.setAttributes(List.of(attributeRequest));
    }


    @Nested
    @DisplayName("createProduct")
    class CreateProduct {

        @Test
        @DisplayName("creates product successfully when all data is valid")
        void createProduct_success() {
            when(productRepository.existsByBarcode("5000000003")).thenReturn(false);
            when(productRepository.existsBySlug(anyString())).thenReturn(false);
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(productMapper.toEntity(productRequest, category)).thenReturn(product);
            when(productRepository.save(product)).thenReturn(product);

            ProductResponse response = productService.createProduct(productRequest);

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Oat Milk");
            verify(productRepository).save(product);
        }

        @Test
        @DisplayName("throws DuplicateResourceException when barcode already exists")
        void createProduct_duplicateBarcode_throwsDuplicateResourceException() {
            when(productRepository.existsByBarcode("5000000003")).thenReturn(true);

            assertThatThrownBy(() -> productService.createProduct(productRequest))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("5000000003");

            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when category does not exist")
        void createProduct_categoryNotFound_throwsResourceNotFoundException() {
            when(productRepository.existsByBarcode("5000000003")).thenReturn(false);
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.createProduct(productRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining(categoryId.toString());

            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("generates slug from product name")
        void createProduct_generatesSlugFromName() {
            when(productRepository.existsByBarcode("5000000003")).thenReturn(false);
            when(productRepository.existsBySlug("oat-milk")).thenReturn(false);
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(productMapper.toEntity(productRequest, category)).thenReturn(product);
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            productService.createProduct(productRequest);

            verify(productRepository).save(argThat(p -> "oat-milk".equals(p.getSlug())));
        }



        @Test
        @DisplayName("sets isActive to true on creation")
        void createProduct_setsIsActiveTrue() {
            when(productRepository.existsByBarcode("5000000003")).thenReturn(false);
            when(productRepository.existsBySlug(anyString())).thenReturn(false);
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

            Product newProduct = new Product();
            newProduct.setName("Oat Milk");
            when(productMapper.toEntity(productRequest, category)).thenReturn(newProduct);
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            productService.createProduct(productRequest);

            assertThat(newProduct.isActive()).isTrue();
        }

        @Test
        @DisplayName("throws DuplicateResourceException on race condition via DataIntegrityViolationException")
        void createProduct_raceCondition_throwsDuplicateResourceException() {
            when(productRepository.existsByBarcode("5000000003")).thenReturn(false);
            when(productRepository.existsBySlug(anyString())).thenReturn(false);
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(productMapper.toEntity(productRequest, category)).thenReturn(product);
            when(productRepository.save(any())).thenThrow(new DataIntegrityViolationException("duplicate"));

            assertThatThrownBy(() -> productService.createProduct(productRequest))
                    .isInstanceOf(DuplicateResourceException.class);
        }

        @Test
        @DisplayName("creates product with null barcode successfully")
        void createProduct_nullBarcode_success() {
            productRequest.setBarcode(null);

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(productRepository.existsBySlug(anyString())).thenReturn(false);
            when(productMapper.toEntity(productRequest, category)).thenReturn(product);
            when(productRepository.save(product)).thenReturn(product);

            ProductResponse response = productService.createProduct(productRequest);

            assertThat(response).isNotNull();
            // existsByBarcode should never be called when barcode is null
            verify(productRepository, never()).existsByBarcode(any());
        }
    }







    @Nested
    @DisplayName("getProductById")
    class GetProductById {

        @Test
        @DisplayName("returns product response when product exists")
        void getProductById_success() {
            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));

            ProductResponse response = productService.getProductById(productId);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(productId);
            assertThat(response.getName()).isEqualTo("Oat Milk");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when product does not exist")
        void getProductById_notFound_throwsResourceNotFoundException() {
            when(productRepository.findById(productId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.getProductById(productId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining(productId.toString());
        }

        @Test
        @DisplayName("returns product with images populated")
        void getProductById_returnsProductWithImages() {
            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));

            ProductResponse response = productService.getProductById(productId);

            assertThat(response.getImages()).isNotEmpty();
            assertThat(response.getImages().get(0).getUrl())
                    .isEqualTo("/images/oat-milk-1.jpg");
        }

        @Test
        @DisplayName("returns product with attributes populated")
        void getProductById_returnsProductWithAttributes() {
            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));

            ProductResponse response = productService.getProductById(productId);

            assertThat(response.getAttributes()).isNotEmpty();
            assertThat(response.getAttributes().get(0).getAttrKey()).isEqualTo("vegan");
        }
    }







    @Nested
    @DisplayName("getSummaryProducts")
    class GetAllProducts {

        @Test
        @DisplayName("returns list of active product summaries")
        void getAllProducts_returnsActiveProducts() {
            Product product2 = new Product();
            product2.setId(UUID.randomUUID());
            product2.setName("Almond Milk");
            product2.setSlug("almond-milk");
            product2.setActive(true);

            when(productRepository.findAllActiveWithPrimaryImage())
                    .thenReturn(List.of(product, product2));

            List<ProductSummaryResponse> responses = productService.getSummaryProducts();

            assertThat(responses).hasSize(2);
            assertThat(responses).extracting(ProductSummaryResponse::getName)
                    .containsExactly("Oat Milk", "Almond Milk");
        }

        @Test
        @DisplayName("returns empty list when no active products exist")
        void getAllProducts_noActiveProducts_returnsEmptyList() {
            when(productRepository.findAllActiveWithPrimaryImage())
                    .thenReturn(Collections.emptyList());

            List<ProductSummaryResponse> responses = productService.getSummaryProducts();

            assertThat(responses).isEmpty();
        }

        @Test
        @DisplayName("returns primary image URL in summary")
        void getAllProducts_returnsPrimaryImageUrl() {
            when(productRepository.findAllActiveWithPrimaryImage())
                    .thenReturn(List.of(product));

            List<ProductSummaryResponse> responses = productService.getSummaryProducts();

            assertThat(responses.get(0).getPrimaryImageUrl())
                    .isEqualTo("/images/oat-milk-1.jpg");
        }
    }






    @Nested
    @DisplayName("getProductsByCategory")
    class GetProductsByCategory {

        @Test
        @DisplayName("returns products in category and all subcategories")
        void getProductsByCategory_success() {
            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(category));
            when(productRepository.findProductIdsByCategoryAndDescendants(categoryId))
                    .thenReturn(List.of(productId));
            when(productRepository.findByIdInWithImages(List.of(productId)))
                    .thenReturn(List.of(product));

            List<ProductSummaryResponse> responses =
                    productService.getProductsByCategory(categoryId);

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getName()).isEqualTo("Oat Milk");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when category does not exist")
        void getProductsByCategory_categoryNotFound_throwsResourceNotFoundException() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.getProductsByCategory(categoryId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining(categoryId.toString());
        }

        @Test
        @DisplayName("returns empty list when category has no products")
        void getProductsByCategory_noProducts_returnsEmptyList() {
            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(category));
            when(productRepository.findProductIdsByCategoryAndDescendants(categoryId))
                    .thenReturn(Collections.emptyList());

            List<ProductSummaryResponse> responses =
                    productService.getProductsByCategory(categoryId);

            assertThat(responses).isEmpty();
            verify(productRepository, never()).findByIdInWithImages(any());
        }
    }






    @Nested
    @DisplayName("updateProduct")
    class UpdateProduct {

        @Test
        @DisplayName("updates product successfully when all data is valid")
        void updateProduct_success() {
            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));
            when(productRepository.existsByBarcodeAndIdNot("5000000003", productId))
                    .thenReturn(false);
            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(category));
            when(productRepository.save(product)).thenReturn(product);

            ProductResponse response = productService.updateProduct(productRequest, productId);

            assertThat(response).isNotNull();
            verify(productMapper).updateEntity(product, productRequest, category);
            verify(productRepository).save(product);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when product does not exist")
        void updateProduct_notFound_throwsResourceNotFoundException() {
            when(productRepository.findById(productId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.updateProduct(productRequest, productId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining(productId.toString());

            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws DuplicateResourceException when barcode belongs to another product")
        void updateProduct_duplicateBarcode_throwsDuplicateResourceException() {
            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));
            when(productRepository.existsByBarcodeAndIdNot("5000000003", productId))
                    .thenReturn(true);

            assertThatThrownBy(() -> productService.updateProduct(productRequest, productId))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("5000000003");

            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("regenerates slug when name changes")
        void updateProduct_nameChanged_regeneratesSlug() {
            productRequest.setName("Oat Drink");
            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));
            when(productRepository.existsByBarcodeAndIdNot(any(), any()))
                    .thenReturn(false);
            when(productRepository.existsBySlug("oat-drink")).thenReturn(false);
            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(category));
            when(productRepository.save(any(Product.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            productService.updateProduct(productRequest, productId);

            verify(productRepository).save(argThat(p -> "oat-drink".equals(p.getSlug())));
        }

        @Test
        @DisplayName("does not regenerate slug when name is unchanged")
        void updateProduct_nameUnchanged_doesNotRegenerateSlug() {
            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));
            when(productRepository.existsByBarcodeAndIdNot(any(), any()))
                    .thenReturn(false);
            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(category));
            when(productRepository.save(product)).thenReturn(product);

            productService.updateProduct(productRequest, productId);

            // slug should remain "oat-milk" — existsBySlug never called
            verify(productRepository, never()).existsBySlug(anyString());
            assertThat(product.getSlug()).isEqualTo("oat-milk");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when new category does not exist")
        void updateProduct_categoryNotFound_throwsResourceNotFoundException() {
            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));
            when(productRepository.existsByBarcodeAndIdNot(any(), any()))
                    .thenReturn(false);
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.updateProduct(productRequest, productId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining(categoryId.toString());

            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws DuplicateResourceException on race condition")
        void updateProduct_raceCondition_throwsDuplicateResourceException() {
            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));
            when(productRepository.existsByBarcodeAndIdNot(any(), any()))
                    .thenReturn(false);
            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(category));
            when(productRepository.save(any()))
                    .thenThrow(new DataIntegrityViolationException("duplicate"));

            assertThatThrownBy(() -> productService.updateProduct(productRequest, productId))
                    .isInstanceOf(DuplicateResourceException.class);
        }
    }





    @Nested
    @DisplayName("deleteProduct")
    class DeleteProduct {

        @Test
        @DisplayName("soft deletes product by setting isActive to false")
        void deleteProduct_success() {
            when(productRepository.deactivateById(productId)).thenReturn(1);

            productService.deleteProduct(productId);

            verify(productRepository).deactivateById(productId);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when product does not exist")
        void deleteProduct_notFound_throwsResourceNotFoundException() {
            when(productRepository.deactivateById(productId)).thenReturn(0);

            assertThatThrownBy(() -> productService.deleteProduct(productId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining(productId.toString());
        }
    }






    @Nested
    @DisplayName("slug generation")
    class SlugGeneration {

        @Test
        @DisplayName("slugifies name with special characters correctly")
        void createProduct_specialCharactersInName_slugifiedCorrectly() {
            productRequest.setName("Dairy & Eggs Special!");
            when(productRepository.existsByBarcode(any())).thenReturn(false);
            when(productRepository.existsBySlug("dairy-and-eggs-special"))
                    .thenReturn(false);
            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(category));

            Product newProduct = new Product();
            newProduct.setName("Dairy & Eggs Special!");
            when(productMapper.toEntity(productRequest, category)).thenReturn(newProduct);
            when(productRepository.save(any(Product.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            productService.createProduct(productRequest);

            verify(productRepository).save(argThat(p ->
                    "dairy-and-eggs-special".equals(p.getSlug())));
        }


    }
}