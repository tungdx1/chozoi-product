package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.Attribute;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AttributeMDRepository extends MongoRepository<Attribute, Integer> {
    List<Attribute> findByCategoryIdAndState(Integer id, String state);
}
