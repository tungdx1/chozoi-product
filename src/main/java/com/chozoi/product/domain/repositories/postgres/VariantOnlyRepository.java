package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.ProductVariant;
import com.chozoi.product.domain.entities.postgres.ProductVariantOnly;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariantOnlyRepository extends JpaRepository<ProductVariantOnly, Long> {
}
