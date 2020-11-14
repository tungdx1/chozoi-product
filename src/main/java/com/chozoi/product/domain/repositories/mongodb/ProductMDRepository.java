package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.ProductMongo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductMDRepository extends MongoRepository<ProductMongo, Long> {

    ProductMongo findAllById(Long productId);

    Page<ProductMongo> findByIdIn(List<Long> ids, Pageable pageable);
}
