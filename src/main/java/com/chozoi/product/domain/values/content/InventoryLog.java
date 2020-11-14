package com.chozoi.product.domain.values.content;

import com.chozoi.product.domain.entities.postgres.types.InventoryHistoryState;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class InventoryLog extends ProductContent {
    private Integer initialQuantity;

    private Integer remainingQuantity;
    private Integer outQuantity;
    private Integer quantity;

    private Integer preRemainingQuantity;
    private Integer preOutQuantity;
    private Integer preQuantity;

    private InventoryHistoryState state;
    private Long variantId;

}
