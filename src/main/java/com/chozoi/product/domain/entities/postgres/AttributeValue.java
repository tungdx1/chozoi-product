package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.entities.postgres.types.AttributeValueState;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(schema = "products", name = "attribute_value_view")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
@Immutable
@Data
@NoArgsConstructor
@Where(clause = "state='PUBLIC'")
public class AttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "value")
    private String value;

    @Column(name = "attribute_id")
    private Integer attributeId;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, columnDefinition = "state")
    @Type(type = "pgsql_enum")
    private AttributeValueState state;

    @ManyToOne
    @JoinColumn(
            name = "attribute_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false)
    @JsonIgnore
    private Attribute attribute;
}
