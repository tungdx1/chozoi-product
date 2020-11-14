package com.chozoi.product.domain.entities.mongodb;


import com.chozoi.product.data.request.AttributeVariant;
import com.chozoi.product.domain.entities.abstracts.ProductVariantAbstract;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(value = "products.product.variant")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductVariant extends ProductVariantAbstract {

    @Id
    private Long id;

    private Long productId;

    private Long price;

    private Long salePrice;

    private String sku;

    private Long imageId;

    private List<AttributeVariant> attributes;

    private Long createdAt;

    private Long updatedAt;

    private InventoryMongo inventory;
}
