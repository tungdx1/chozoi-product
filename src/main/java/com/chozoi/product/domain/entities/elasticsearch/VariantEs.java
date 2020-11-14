package com.chozoi.product.domain.entities.elasticsearch;

import com.chozoi.product.domain.entities.abstracts.ProductVariantAbstract;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
public class VariantEs extends ProductVariantAbstract {
    private Long id;
    private Long price;
    private Long salePrice;

    @Field(type = FieldType.Text)
    private String sku;

    private List<AttributeEs> attributes;
    private InventoryEs inventory;

    public void sync(VariantEs variantEs, int remainingQuantity) {
        this.id = variantEs.id;
        this.price = variantEs.price;
        this.salePrice = variantEs.salePrice;
        this.sku = variantEs.sku;
        this.attributes = variantEs.attributes;
        if (remainingQuantity == 0) {
            InventoryEs inventoryEs = new InventoryEs();
            inventoryEs.sync(variantEs.getInventory(), remainingQuantity);
            this.inventory = inventoryEs;
        }
    }
}
