package com.chozoi.product.domain.repositories.redis;

import com.chozoi.product.domain.entities.redis.HomeProduct;
import org.springframework.data.repository.CrudRepository;

public interface HomeRedisRepository extends CrudRepository<HomeProduct, String> {
}
