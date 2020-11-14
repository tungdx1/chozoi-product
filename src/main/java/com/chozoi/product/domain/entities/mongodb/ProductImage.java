package com.chozoi.product.domain.entities.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "products.product.image")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

    @Id
    private Long id;

    private Long productId;

    private Long productVariantId;

    private String imageUrl;

    private String state;

    private Integer sort;

    private Long createdAt;

    private Long updatedAt;

    public static enum State {
        PUBLIC,
        DELETED
    }
}
