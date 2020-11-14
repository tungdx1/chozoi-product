package com.chozoi.product.data.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class RatingStats implements Serializable {
    private Integer one;
    private Integer two;
    private Integer three;
    private Integer four;
    private Integer five;
    private Integer total;
    private Double average;
}
