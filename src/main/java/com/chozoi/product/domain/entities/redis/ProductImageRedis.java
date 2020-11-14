package com.chozoi.product.domain.entities.redis;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@Builder
@RedisHash(value = "ProductImage", timeToLive = 600)
public class ProductImageRedis {
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
