package com.chozoi.product.domain.repositories.redis;

import com.chozoi.product.domain.entities.redis.ProductImageRedis;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductImageRedisRepository extends CrudRepository<ProductImageRedis, Long> {
    List<ProductImageRedis> findByProductId(long productId);
}
