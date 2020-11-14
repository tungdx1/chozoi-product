package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.ShopStats;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShopStatsMDRepository extends MongoRepository<ShopStats, Integer> {
}
