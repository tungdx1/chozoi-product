package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.InventorySuccess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventorySuccessRepository extends JpaRepository<InventorySuccess, Long> {
}
