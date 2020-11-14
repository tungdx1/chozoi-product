package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeValueRepository extends JpaRepository<AttributeValue, Integer> {
}
