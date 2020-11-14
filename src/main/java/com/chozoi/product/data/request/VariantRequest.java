package com.chozoi.product.data.request;

import lombok.Data;

@Data
public class VariantRequest {
    private String attribute_1;
    private String attribute_2;
    private String value_1;
    private String value_2;
    private long price;
    private Integer quantity;
    private String sku;
}
