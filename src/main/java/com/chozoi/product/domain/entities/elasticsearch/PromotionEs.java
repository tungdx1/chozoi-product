package com.chozoi.product.domain.entities.elasticsearch;

import lombok.Data;

@Data
public class PromotionEs {
    private Long id;
    private Long price;
    private Long quantity;
    private Long salePrice;
    private Long soldQuantity;
    //  private Long updatedAt;
//  private Long createdAt;
    private Long dateEnd;
    private Long dateStart;
}
