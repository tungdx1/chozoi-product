package com.chozoi.product.domain.entities.elasticsearch;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
public class ClassifiersEs {
    @Field(type = FieldType.Text)
    private String name;

    private List<String> values;
}
