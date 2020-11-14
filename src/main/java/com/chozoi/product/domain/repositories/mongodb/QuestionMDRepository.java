package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.Question;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuestionMDRepository extends MongoRepository<Question, Long> {
}
