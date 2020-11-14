package com.chozoi.product.domain.entities.mongodb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "products.attribute")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attribute {

    @Id
    private Integer id;

    private Integer categoryId;

    private String name;

    private boolean isRequired;

    private String validation;

    private String type;

    private String state;

    @JsonInclude(Include.NON_EMPTY)
    private List<AttributeValue> values;

    private Long createdAt;

    private Long updatedAt;
}
