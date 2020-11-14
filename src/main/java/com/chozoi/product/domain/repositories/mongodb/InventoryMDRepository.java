package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.InventoryMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InventoryMDRepository extends MongoRepository<InventoryMongo, Long> {
    List<InventoryMongo> findAllByProductId(long productId);
}
