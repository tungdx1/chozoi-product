package com.chozoi.product.app.dtos.elasticsearch;

import lombok.Data;

@Data
public class CategoriesDTO {
    private Integer id;
    private Integer parentId;
    private Integer level;
    private Integer sort;
    private String name;
    private String avatarUrl;
    private String description;
    private String state;
}
