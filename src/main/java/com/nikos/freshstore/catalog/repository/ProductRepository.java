package com.nikos.freshstore.catalog.repository;

import com.nikos.freshstore.catalog.domain.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @EntityGraph(attributePaths = { "images", "attributes" })
    Optional<Product> findById(UUID id);

    @Query("""
        SELECT p FROM Product p
        LEFT JOIN FETCH p.images img
        WHERE p.isActive = true
        AND (img IS NULL OR img.isPrimary = true)
        ORDER BY p.name ASC
        """)
    List<Product> findAllActiveWithPrimaryImage();

    boolean existsByBarcode(String barcode);

    boolean existsByBarcodeAndIdNot(String barcode, UUID id);

    boolean existsBySlug(String slug);

    @Modifying
    @Query("UPDATE Product p SET p.isActive = false WHERE p.id = :id")
    int deactivateById(@Param("id") UUID id);

    @Query(value = """
            WITH RECURSIVE subcategories AS (
                SELECT id FROM catalog.category
                WHERE id = :categoryId
                UNION ALL
                SELECT c.id FROM catalog.category c
                INNER JOIN subcategories sc ON c.parent_id = sc.id
            )
            SELECT p.id FROM catalog.product p
            WHERE p.category_id IN (SELECT id FROM subcategories)
            AND p.is_active = true
            ORDER BY p.name ASC
            """, nativeQuery = true)
    List<UUID> findProductIdsByCategoryAndDescendants(@Param("categoryId") UUID categoryId);

    @Query("""
            SELECT p FROM Product p
            LEFT JOIN FETCH p.images
            WHERE p.id IN :ids
            ORDER BY p.name ASC
            """)
    List<Product> findByIdInWithImages(@Param("ids") List<UUID> ids);

}
