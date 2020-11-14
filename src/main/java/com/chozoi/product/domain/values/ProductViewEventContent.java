package com.chozoi.product.domain.values;

import com.chozoi.product.domain.entities.postgres.EventContent;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ProductViewEventContent extends EventContent {
    private Long id;
    private Integer shopId;
    private Integer categoryId;
    private ProductType type;
    private ProductState state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
