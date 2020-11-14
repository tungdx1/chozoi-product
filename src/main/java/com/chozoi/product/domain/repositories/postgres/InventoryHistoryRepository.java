package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.InventoryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, Long> {
}
