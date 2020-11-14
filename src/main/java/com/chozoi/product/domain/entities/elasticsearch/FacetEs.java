package com.chozoi.product.domain.entities.elasticsearch;

import lombok.Data;

@Data
public class FacetEs {
    private Integer categoryLevel1;
    private Integer categoryLevel2;
    private Integer categoryLevel3;
}
