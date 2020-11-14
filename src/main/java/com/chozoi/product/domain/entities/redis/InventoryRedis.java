package com.chozoi.product.domain.entities.redis;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.util.Objects;

@Data
@Builder
@RedisHash(value = "ProductInventory", timeToLive = 60)
public class InventoryRedis {
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
