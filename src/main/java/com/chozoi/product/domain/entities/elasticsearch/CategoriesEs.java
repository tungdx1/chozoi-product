package com.chozoi.product.domain.entities.elasticsearch;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;

@Data
@Document(indexName = "chozoi_categories", type = "_doc")
public class CategoriesEs {
    @Id
    private Integer id;

    private Integer parentId;

    private Integer level;

    private Integer sort;

    private String name;

    @Field(type = FieldType.Keyword)
    private String avatarUrl;

    @Field(type = FieldType.Text)
    private String description;

    private String state;

//  private Long createdAt;
//
//  private Long updatedAt;
}
