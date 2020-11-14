package com.chozoi.product.domain.entities.postgres;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "inventory_success", schema = "sales")
public class InventorySuccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(type = "pg-array")
    @Column(name = "keep_ids")
    private Integer[] keepIds;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
