package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryMDRepository extends MongoRepository<Category, Integer> {
}
