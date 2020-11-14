package com.chozoi.product.data.response;

import lombok.Data;

@Data
public class CategoryData {
    private Integer id;
    private String name;
    private Integer parentId;
}
