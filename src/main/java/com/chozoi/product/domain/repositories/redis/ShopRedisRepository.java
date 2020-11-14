package com.chozoi.product.domain.repositories.redis;

import com.chozoi.product.domain.entities.redis.ShopRedis;
import org.springframework.data.repository.CrudRepository;

public interface ShopRedisRepository extends CrudRepository<ShopRedis, Integer> {
}
