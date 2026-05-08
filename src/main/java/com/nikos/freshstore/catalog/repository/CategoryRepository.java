package com.nikos.freshstore.catalog.repository;

import com.nikos.freshstore.catalog.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    @Query(value = """
        WITH RECURSIVE descendants AS (
            SELECT id, name, slug, parent_id, sort_order, 1 AS depth
            FROM catalog.category
            WHERE parent_id = :parentId
            UNION ALL
            SELECT c.id, c.name, c.slug, c.parent_id, c.sort_order, sc.depth + 1
            FROM catalog.category c
            INNER JOIN descendants sc ON c.parent_id = sc.id
            WHERE sc.depth < 10
        )
        SELECT id, name, slug, parent_id, sort_order
        FROM descendants
        ORDER BY depth, sort_order, name
        """, nativeQuery = true)
    List<Category> findAllDescendants(@Param("parentId") UUID parentId);

    @Query(value = """
        WITH RECURSIVE tree AS (
            SELECT id, name, slug, parent_id, sort_order, 0 AS depth
            FROM catalog.category
            WHERE parent_id IS NULL
            UNION ALL
            SELECT c.id, c.name, c.slug, c.parent_id, c.sort_order, t.depth + 1
            FROM catalog.category c
            INNER JOIN tree t ON c.parent_id = t.id
            WHERE t.depth < 10
        )
        SELECT id, name, slug, parent_id, sort_order
        FROM tree
        ORDER BY depth, sort_order, name
        """, nativeQuery = true)
    List<Category> findFullTree();

    List<Category> findByParentIdOrderBySortOrderAscNameAsc(UUID parentId);

    boolean existsByNameIgnoreCaseAndParent(String name, Category parent);
    boolean existsByNameIgnoreCaseAndParentAndIdNot(String name, Category parent, UUID id);

    boolean existsByNameIgnoreCaseAndParentIsNull(String name);
    boolean existsByNameIgnoreCaseAndParentIsNullAndIdNot(String name, UUID id);

    List<Category> findByParentIsNullOrderBySortOrderAscNameAsc();

    boolean existsBySlug(String slug);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.parent WHERE c.id = :id")
    Optional<Category> findByIdWithParent(@Param("id") UUID id);

    @Modifying
    @Query("DELETE FROM Category c WHERE c.id = :id")
    int deleteByIdReturningCount(@Param("id") UUID id);

    boolean existsByParentId(UUID id);
}
