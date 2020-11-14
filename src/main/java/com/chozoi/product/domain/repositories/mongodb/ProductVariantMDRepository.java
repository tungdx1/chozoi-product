package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.ProductVariant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductVariantMDRepository extends MongoRepository<ProductVariant, Long> {
    ProductVariant findByInventoryId(Long id);

    List<ProductVariant> findByProductId(Long id);
}
