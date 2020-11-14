package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.ProductStatsMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductStatsMDRepository extends MongoRepository<ProductStatsMongo, Long> {
}
