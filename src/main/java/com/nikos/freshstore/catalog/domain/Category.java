package com.nikos.freshstore.catalog.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
@Entity
@Table(
        name = "category",
        schema = "catalog",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_category_name_parent",
                        columnNames = { "name", "parent_id" }
                ),
                @UniqueConstraint(
                        name = "uq_category_slug",
                        columnNames = { "slug" }
                )
        }
)
public class Category {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "slug", nullable = false, length = 100)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Column(name="sort_order")
    private int sortOrder = 0;
}
