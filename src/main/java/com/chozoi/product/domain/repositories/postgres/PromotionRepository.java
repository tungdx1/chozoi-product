package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
}
