package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.AuctionPhase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionPhaseRepository extends JpaRepository<AuctionPhase, Long> {
    List<AuctionPhase> findByIdIn(List<Long> phaseIds);
}
