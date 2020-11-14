package com.chozoi.product.domain.entities.elasticsearch;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class CategoryEs {

    @Field(type = FieldType.Keyword)
    private String avatarUrl;

    @Field(type = FieldType.Text)
    private String description;

    private Integer id;

    @Field(type = FieldType.Text)
    private String name;

    private Integer parentId;

    private Integer sort;

    @Field(type = FieldType.Keyword)
    private String state;

//  private Long createdAt;
//
//  private Long updatedAt;
}
