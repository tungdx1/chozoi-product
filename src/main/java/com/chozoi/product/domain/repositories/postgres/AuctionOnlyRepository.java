package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.elasticsearch.AuctionEs;
import com.chozoi.product.domain.entities.postgres.AuctionOnly;
import com.chozoi.product.domain.entities.postgres.types.ProductAuctionState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuctionOnlyRepository extends JpaRepository<AuctionOnly, Long> {
  List<AuctionOnly> findByTimeEndBetween(LocalDateTime start, LocalDateTime end);

  List<AuctionOnly> findByStateAndTimeEndBetween(
      ProductAuctionState state, LocalDateTime start, LocalDateTime end);

  List<AuctionOnly> findByState(ProductAuctionState state);
}
