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

@Document(collection = "products.category")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    private Integer id;

    private Integer parentId;

    private Integer level;

    private Integer sort;

    private String name;

    private String avatarUrl;

    private String description;

    private String state;

    @JsonInclude(Include.NON_EMPTY)
    private List<Attribute> attributes;

    private Long createdAt;

    private Long updatedAt;
}
