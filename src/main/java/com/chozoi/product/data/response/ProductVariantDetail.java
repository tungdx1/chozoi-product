package com.chozoi.product.data.response;

import com.chozoi.product.data.request.AttributeVariant;
import com.chozoi.product.data.response.abstracts.VariantAbstract;
import lombok.Data;

import java.util.List;

@Data
public class ProductVariantDetail extends VariantAbstract {
    private Long id;

    private Long price;

    private Long salePrice;

    private String sku;

    private Long imageId;

    private List<AttributeVariant> attributes;

    private InventoryResponse inventory;
}
