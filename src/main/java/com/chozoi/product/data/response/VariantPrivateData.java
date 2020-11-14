package com.chozoi.product.data.response;

import lombok.Data;


@Data
public class VariantPrivateData {
    private Long id;
    private Long price;
    private Long salePrice;
    private String sku;
    private Long imageId;
    private InventoryResponse inventory;
}