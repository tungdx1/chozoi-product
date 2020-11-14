package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.ProductImage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ImageMDRepository extends MongoRepository<ProductImage, Long> {
    List<ProductImage> findByProductIdAndState(Long id, String state);
}
