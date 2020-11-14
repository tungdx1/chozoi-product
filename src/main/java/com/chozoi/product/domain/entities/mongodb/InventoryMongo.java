package com.chozoi.product.domain.entities.mongodb;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(value = "products.product.inventory")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Log4j2
public class InventoryMongo {

    private Long id; // variantId

    private Integer initialQuantity;

    private Integer inQuantity;

    private Integer outQuantity;

    private Integer remainingQuantity;

    private Long productId;

    private Long createdAt;

    private Long updatedAt;

    public Integer getRemainingQuantity() {

        if (Objects.nonNull(initialQuantity)
                && Objects.nonNull(inQuantity)
                && Objects.nonNull(outQuantity)) {
            remainingQuantity = initialQuantity + inQuantity - outQuantity;
        }

        return remainingQuantity;
    }
}
