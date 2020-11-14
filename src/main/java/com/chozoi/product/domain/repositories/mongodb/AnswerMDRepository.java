package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.Answer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnswerMDRepository extends MongoRepository<Answer, Long> {
}
