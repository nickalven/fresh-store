package com.nikos.freshstore.catalog.repository;

import com.nikos.freshstore.catalog.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);

    boolean existsBySlug(String slug);

    @Modifying
    @Query("DELETE FROM Store s WHERE s.id = :id")
    int deleteByIdReturningCount(@Param("id") UUID id);
}
