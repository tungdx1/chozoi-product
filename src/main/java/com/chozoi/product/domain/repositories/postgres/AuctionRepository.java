package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
  List<Auction> findByTimeEndBetween(LocalDateTime start, LocalDateTime end);
}
