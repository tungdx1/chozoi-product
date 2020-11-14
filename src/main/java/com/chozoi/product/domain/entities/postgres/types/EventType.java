package com.chozoi.product.domain.entities.postgres.types;

public enum EventType {
    ProductCreated,
    ProductUpdated,
    ProductChangeStated,
    ProductViewed,
    ProductQuantityChanged,
    ProductPriceChanged,
    InventoryCreated
}
