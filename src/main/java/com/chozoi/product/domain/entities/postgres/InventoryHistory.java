package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.entities.postgres.types.InventoryHistoryState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "inventory_history", schema = "products")
public class InventoryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    @JsonIgnore
    private ProductVariant variant;

    @Enumerated(value = EnumType.STRING)
    @org.hibernate.annotations.Type(type = "pg-enum")
    @Column(nullable = false, columnDefinition = "inventory_type", name = "type")
    private InventoryHistoryState type;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void assign(ProductVariant variant) {
        this.variant = variant;
        this.type = InventoryHistoryState.INITIALIZED;
        this.quantity = variant.getInventory().getInQuantity();
    }
}
