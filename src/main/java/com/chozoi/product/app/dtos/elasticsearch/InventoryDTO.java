package com.chozoi.product.app.dtos.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InventoryDTO {
    private Long id;
    private Long initialQuantity;
    private Long inQuantity;
    private Long outQuantity;
    private Long remainingQuantity;

    @JsonProperty(value = "quantity", access = JsonProperty.Access.READ_ONLY)
    private Long quantity() {
        return initialQuantity + inQuantity - outQuantity;
    }
}
