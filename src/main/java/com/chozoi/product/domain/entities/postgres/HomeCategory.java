package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.entities.postgres.types.CategoryState;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "category_view", schema = "products")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
@Where(clause = "state = 'PUBLIC'")
@Data
@NoArgsConstructor
public class HomeCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, columnDefinition = "category_state")
    @Type(type = "pgsql_enum")
    private CategoryState state;

    private Integer sort;

    private Integer level;

    @Column(name = "parent_id")
    private Integer parentId;
}
