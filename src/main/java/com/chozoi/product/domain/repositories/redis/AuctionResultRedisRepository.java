package com.chozoi.product.domain.repositories.redis;

import com.chozoi.product.domain.entities.redis.AuctionResultRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionResultRedisRepository extends CrudRepository<AuctionResultRedis, Long> {
}