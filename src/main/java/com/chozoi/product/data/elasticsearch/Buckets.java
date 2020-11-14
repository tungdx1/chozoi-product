package com.chozoi.product.data.elasticsearch;

import lombok.Data;

@Data
public class Buckets {
    private int docCount;
    private int docCountError;
    private Aggregations aggregations;
    private int key;
    private int keyAsNumber;
    private int keyAsString;
    private Boolean fragment;
}
