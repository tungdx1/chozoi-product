package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.Order;
import com.chozoi.product.domain.entities.postgres.OrderGuardFlashBid;
import com.chozoi.product.domain.entities.types.ShopOrderState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderGuardFlashBidRepository extends JpaRepository<OrderGuardFlashBid, Long> {

    List<OrderGuardFlashBid> findByPhaseIdIn(List<Long> phaseIds);
}
