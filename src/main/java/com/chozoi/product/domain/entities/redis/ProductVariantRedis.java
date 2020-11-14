package com.chozoi.product.domain.entities.redis;

import com.chozoi.product.data.request.AttributeVariant;
import lombok.Data;

import java.util.List;

@Data
public class ProductVariantRedis {
    private Long id;

    private Long productId;

    private Long price;

    private Long salePrice;

    private String sku;

    private Long imageId;

    private List<AttributeVariant> attributes;

    private InventoryRedis inventory;
}
