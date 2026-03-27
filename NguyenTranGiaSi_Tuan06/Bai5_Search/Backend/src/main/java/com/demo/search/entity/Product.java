package com.demo.search.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@NamedStoredProcedureQuery(
    name = "Product.spSearch",
    procedureName = "sp_search_products",
    resultClasses = Product.class,
    parameters = @StoredProcedureParameter(
        mode = ParameterMode.IN,
        name = "p_keyword",
        type = String.class
    )
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String category;

    private Double price;

    @Column(columnDefinition = "TEXT")
    private String description;
}
