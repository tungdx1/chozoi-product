package com.chozoi.product.domain.repositories.redis;

import com.chozoi.product.domain.entities.redis.config_home.ProductGroupRedis;
import org.springframework.data.repository.CrudRepository;

public interface ProductGroupRedisRepository extends CrudRepository<ProductGroupRedis, Integer> {}
