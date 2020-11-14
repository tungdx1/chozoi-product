package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.ShippingSelect;
import com.chozoi.product.domain.entities.postgres.types.ShippingSelectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingSelectRepository extends JpaRepository<ShippingSelect, Integer> {
    List<ShippingSelect> findByShopIdAndStatus(Integer shop_id, ShippingSelectStatus status);
}
