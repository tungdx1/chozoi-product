package com.chozoi.product.domain.repositories.redis;

import com.chozoi.product.domain.entities.redis.ProductRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRedisRepository extends CrudRepository<ProductRedis, Long> {
}
