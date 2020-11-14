package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.AuctionParticipantFlashBid;
import com.chozoi.product.domain.entities.postgres.primary_key.AuctionParticipantFlashBidId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionParticipantFlashBidRepository
    extends JpaRepository<AuctionParticipantFlashBid, AuctionParticipantFlashBidId> {
  List<AuctionParticipantFlashBid> findById_UserIdOrderByCreatedAtDesc(Integer userId);

  Page<AuctionParticipantFlashBid> findById_UserIdOrderByCreatedAtDesc(
      Integer userId, Pageable page);
}
