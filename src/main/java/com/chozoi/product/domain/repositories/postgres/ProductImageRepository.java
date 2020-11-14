package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.ProductImage;
import com.chozoi.product.domain.entities.postgres.types.ProductImageState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductId(Long productId);

    List<ProductImage> findByProductIdAndState(Long productId, ProductImageState state);

    List<ProductImage> findByProductIdAndStateIn(Long productId, List<ProductImageState> state);
}
