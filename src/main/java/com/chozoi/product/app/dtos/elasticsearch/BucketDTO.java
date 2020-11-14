package com.chozoi.product.app.dtos.elasticsearch;

import lombok.Data;

@Data
public class BucketDTO {
    private int docCount;
    private int key;
}
