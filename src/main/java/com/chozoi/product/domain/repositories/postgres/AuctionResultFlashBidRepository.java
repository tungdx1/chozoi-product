package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.AuctionRessultFlashBid;
import com.chozoi.product.domain.entities.postgres.primary_key.AuctionResultFlashBidId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionResultFlashBidRepository
    extends JpaRepository<AuctionRessultFlashBid, AuctionResultFlashBidId> {
    List<AuctionRessultFlashBid> findAllByIdPhaseIdIn(List<Long> phaseIds);
}
