package com.chozoi.product.data.response;

import lombok.Data;

@Data
public class CategoriesResponse {
    private Integer id;
    private Integer parentId;
    private Integer level;
    private Integer sort;
    private String name;
    private String avatarUrl;
    private String description;
}
