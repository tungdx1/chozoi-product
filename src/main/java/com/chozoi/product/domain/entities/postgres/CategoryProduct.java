package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.entities.postgres.types.CategoryState;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "category_view", schema = "products")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class CategoryProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, columnDefinition = "category_state")
    @Type(type = "pgsql_enum")
    private CategoryState state;

    @Column(name = "sort")
    private Integer sort;

    @Column(name = "level")
    private Integer level;

    @Column(name = "parent_id")
    private Integer parentId;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("category")
    @JsonIgnore
    private List<Product> products;
}
