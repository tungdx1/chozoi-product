package com.chozoi.product.domain.repositories.redis;

import com.chozoi.product.domain.entities.redis.config_home.LayoutBlockRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LayoutBlockRedisRepository extends CrudRepository<LayoutBlockRedis, Integer> {
  List<LayoutBlockRedis> findBySiteAndState(String site, String state);
}
