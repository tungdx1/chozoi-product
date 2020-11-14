package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.Attribute;
import com.chozoi.product.domain.entities.postgres.Category;
import com.chozoi.product.domain.entities.postgres.ProductActiveCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductActiveCodeRepository extends JpaRepository<ProductActiveCode, Long> {

    ProductActiveCode findFirstByUserIdAndProductId(Integer userId, Integer productId);
}
