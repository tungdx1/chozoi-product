package com.chozoi.product.data.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AggregationName {
    private Object metaData;
    private String name;
    private List<Buckets> buckets;
    private int docCountError;
    @JsonProperty("writeableName")
    private String writeAbleName;
    private int sumOfOtherDocCounts;
    private String type;
    private Boolean fragment;
    private Boolean mapped;
}
