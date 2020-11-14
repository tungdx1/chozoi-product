package com.chozoi.product.data.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class VariantStats implements Serializable {
    private Long variantId;
    private Integer quantity;
    private Integer soldQuantity;
}
