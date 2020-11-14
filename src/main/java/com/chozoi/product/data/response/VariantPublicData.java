package com.chozoi.product.data.response;

import lombok.Data;

@Data
public class VariantPublicData {
    private Long id;
    private Long price;
    private Long salePrice;
    private String sku;
    private InventoryResponse inventory;
}
