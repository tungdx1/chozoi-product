package com.chozoi.product.domain.repositories.redis;

import com.chozoi.product.domain.entities.redis.InventoryRedis;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InventoryRedisRepository extends CrudRepository<InventoryRedis, Long> {
    List<InventoryRedis> findByProductId(long productId);
}
