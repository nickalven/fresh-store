package com.nikos.freshstore.catalog.repository;

import com.nikos.freshstore.catalog.domain.Product;
import com.nikos.freshstore.catalog.domain.Store;
import com.nikos.freshstore.catalog.domain.StoreProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreProductRepository extends JpaRepository<StoreProduct, UUID> {


    boolean existsByStoreAndProduct(Store store, Product product);

    @Modifying
    @Query("DELETE FROM StoreProduct sp WHERE sp.id = :id")
    int deleteByIdReturningCount(@Param("id") UUID id);

    @Query("""
        SELECT sp FROM StoreProduct sp
        LEFT JOIN FETCH sp.store
        WHERE sp.product = :product
        """)
    List<StoreProduct> findByProductWithStore(@Param("product") Product product);

    @Query("""
        SELECT sp FROM StoreProduct sp
        LEFT JOIN FETCH sp.product p
        LEFT JOIN FETCH p.images
        WHERE sp.store = :store
        """)
    List<StoreProduct> findByStoreWithProduct(@Param("store") Store store);

}
