package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.ProductViewed;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductViewMDRepository extends MongoRepository<ProductViewed, String> {
}
