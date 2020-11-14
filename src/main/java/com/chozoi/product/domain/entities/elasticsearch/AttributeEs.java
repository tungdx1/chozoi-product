package com.chozoi.product.domain.entities.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class AttributeEs {

    @Field(type = FieldType.Text)
    private Integer id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String value;

    @JsonProperty("value_id")
    private Integer valueId;
}
