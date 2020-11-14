package com.chozoi.product.domain.entities.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "comments.review")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    private Long id;

    private Object user;

    private Object product;

    private Integer rating;

    private String text;

    private Object shop;

    private String state;

    private Long createdAt;

    private Long updatedAt;
}
