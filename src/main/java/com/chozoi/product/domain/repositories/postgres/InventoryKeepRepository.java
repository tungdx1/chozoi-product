package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.InventoryKeep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryKeepRepository extends JpaRepository<InventoryKeep, Long> {
}
