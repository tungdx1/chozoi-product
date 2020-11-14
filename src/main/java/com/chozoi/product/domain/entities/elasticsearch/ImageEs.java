package com.chozoi.product.domain.entities.elasticsearch;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class ImageEs {

    private Long id;


    @Field(type = FieldType.Keyword)
    private String imageUrl;

    private Long productId;

    private Integer sort;

    private Long productVariantId;

    @Field(type = FieldType.Keyword)
    private String state;

//  private Long createdAt;
//  private Long updatedAt;
}
