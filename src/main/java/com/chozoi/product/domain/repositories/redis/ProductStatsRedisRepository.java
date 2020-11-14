package com.chozoi.product.domain.repositories.redis;


import com.chozoi.product.domain.entities.redis.ProductStatsRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductStatsRedisRepository extends CrudRepository<ProductStatsRedis, Long> {
}
