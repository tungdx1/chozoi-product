package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewMDRepository extends MongoRepository<Review, Long> {
    Review findAllById(Long id);
}
