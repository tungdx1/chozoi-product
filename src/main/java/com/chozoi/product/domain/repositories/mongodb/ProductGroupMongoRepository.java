package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.config_home.ProductGroupMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductGroupMongoRepository extends MongoRepository<ProductGroupMongo, Integer> {
    List<ProductGroupMongo> findByIdIn(List<Integer> groupIds);
}
