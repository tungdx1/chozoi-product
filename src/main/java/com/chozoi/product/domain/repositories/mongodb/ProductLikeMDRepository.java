package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.ProductLike;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductLikeMDRepository extends MongoRepository<ProductLike, String> {

}
