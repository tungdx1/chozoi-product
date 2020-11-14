package com.chozoi.product.data.response;

import com.chozoi.product.domain.entities.postgres.Attribute;
import lombok.Data;

import java.util.List;

@Data
public class CategoryResponse {
    private Integer id;
    private Integer level;
    private Integer parentId;
    private String name;
    private String avatarUrl;
    private List<Attribute> attributes;
}
