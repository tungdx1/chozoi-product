package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.Province;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProvinceMDRepository extends MongoRepository<Province, Integer> {
}
