package com.chozoi.product.domain.repositories.redis;

import com.chozoi.product.domain.entities.redis.ShopStatsRedis;
import org.springframework.data.repository.CrudRepository;

public interface ShopStatsRedisRepository extends CrudRepository<ShopStatsRedis, Integer> {
}
