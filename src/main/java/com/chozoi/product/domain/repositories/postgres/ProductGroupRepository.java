package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.product_ranking.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductGroupRepository extends JpaRepository<Group, Integer> {}
