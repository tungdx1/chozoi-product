package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "shipping_partner", schema = "sales")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class ShippingPartner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "max_weight")
    private Integer maxWeight;

    @Column(name = "max_value")
    private Long maxValue;

    @Type(type = "pg-array")
    @Column(name = "max_size")
    private Integer[] maxSize;
}
