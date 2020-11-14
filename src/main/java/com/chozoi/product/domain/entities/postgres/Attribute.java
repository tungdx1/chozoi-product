package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.entities.postgres.types.AttributeState;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "attribute_view", schema = "products")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
@Where(clause = "state='PUBLIC'")
public class Attribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, columnDefinition = "attribute_state")
    @Type(type = "pgsql_enum")
    private AttributeState state;

    @Column(name = "is_required")
    private Boolean isRequired;

    @ManyToOne()
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @JsonIgnore
    private Category category;

    @OneToMany(mappedBy = "attribute", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<AttributeValue> values;
}
