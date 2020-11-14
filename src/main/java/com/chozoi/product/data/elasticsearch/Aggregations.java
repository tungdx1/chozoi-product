package com.chozoi.product.data.elasticsearch;

import lombok.Data;

@Data
public class Aggregations {
    private Object asMap;
    private Boolean fragment;
}
