package com.chozoi.product.domain.repositories.mongodb;

import com.chozoi.product.domain.entities.mongodb.ProductImage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductImageMDRepository extends CrudRepository<ProductImage, Long> {
    List<ProductImage> findByProductId(long productId);
}

