package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.ProductStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductStatsRepository extends JpaRepository<ProductStats, Long> {
}
