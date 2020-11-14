package com.chozoi.product.domain.entities.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "products.product.promotion")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

    private Long id;

    private Long price;

    private Long salePrice;

    private Long quantity;

    private Long soldQuantity;

    private Long dateStart;

    private Long dateEnd;

    private Long createdAt;

    private Long updatedAt;
}
