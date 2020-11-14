package com.chozoi.product.domain.values;


import com.chozoi.product.domain.entities.postgres.EventContent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class InventoryEventContent extends EventContent {
    private Long id;
    private Integer initialQuantity;
    private Integer inQuantity;
    private Integer outQuantity;
}
