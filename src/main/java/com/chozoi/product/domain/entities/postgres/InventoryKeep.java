package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.values.content.InventoryLog;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "inventory_keep", schema = "products")
@TypeDef(name = "pg-jsonb", typeClass = JsonBinaryType.class)
public class InventoryKeep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "variant_id")
    private Long variantId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "quantity")
    private Integer quantity;

    @Type(type = "pg-jsonb")
    @Column(name = "data_log", columnDefinition = "jsonb")
    private InventoryLog dataLog;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
