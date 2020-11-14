package com.chozoi.product.app.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class VariantResponse {
    private Long id;
    private Long price;
    private Long salePrice;
    private Integer remainingQuantity;

    public VariantResponse(Long id, Long price, Long salePrice, Integer remainingQuantity) {
        this.id = id;
        this.price = price;
        this.salePrice = salePrice;
        this.remainingQuantity = remainingQuantity;
    }
}
