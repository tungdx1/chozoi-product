package com.chozoi.product.domain.entities.mongodb;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products.attribute.value")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeValue {

    @Id
    private Integer id;

    private String value;

    private String state;

    private Long createdAt;

    private Long updatedAt;
}
