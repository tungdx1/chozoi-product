package com.chozoi.product.domain.entities.postgres;


import com.chozoi.product.data.request.AttributeVariant;
import com.chozoi.product.domain.entities.postgres.types.VariantState;
import com.chozoi.product.domain.utils.GenericArrayUserType;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode()
@Data
@Entity
@NoArgsConstructor
@Table(name = "product_variant", schema = "products")
@TypeDef(name = "pg-enum", typeClass = PostgreSQLEnumType.class)
@TypeDef(name = "pg-jsonb", typeClass = JsonBinaryType.class)
@TypeDef(name = "pg-array", typeClass = GenericArrayUserType.class)
@Where(clause = "state = 'PUBLIC'")
public class ProductVariantOnly {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "price")
    private Long price;

    @Column(name = "sale_price")
    private Long salePrice;

    @Enumerated(value = EnumType.STRING)
    @Type(type = "pg-enum")
    @Column(nullable = false, columnDefinition = "variant_state", name = "state")
    private VariantState state;

    @Column(name = "sku")
    private String sku;

    @Type(type = "jsonb")
    @Column(name = "attributes", columnDefinition = "jsonb")
    private List<AttributeVariant> attributes;

    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
