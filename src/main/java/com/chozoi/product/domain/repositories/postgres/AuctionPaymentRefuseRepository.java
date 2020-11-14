package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.AuctionPaymentRefuse;
import com.chozoi.product.domain.entities.postgres.AuctionPhase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionPaymentRefuseRepository extends JpaRepository<AuctionPaymentRefuse, Long> {
    List<AuctionPaymentRefuse> findByIdPhaseIdIn(List<Long> phaseIds);

    Page<AuctionPaymentRefuse> findByUserId(Integer userId, Pageable pageable);
}
