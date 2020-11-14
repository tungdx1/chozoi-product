package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.entities.postgres.types.CategoryState;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(schema = "products", name = "category")
@TypeDef(name = "pg-enum", typeClass = PostgreSQLEnumType.class)
//@Where(clause = "state = 'PUBLIC'")
public class Category extends com.chozoi.product.domain.entities.abstracts.Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, columnDefinition = "category_state")
    @Type(type = "pg-enum")
    private CategoryState state;

    @Column(name = "avatar_url")
    private String avatarUrl;

    private Integer sort;

    private Integer level;

    @Column(name = "parent_id")
    private Integer parentId;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<Attribute> attributes;
}
