package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.entities.postgres.primary_key.AuctionResultFlashBidId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(schema = "auctions", name = "auction_result_flash_bid")
@Data
@Immutable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionRessultFlashBid {

  @EmbeddedId private AuctionResultFlashBidId id;

  @Column(name = "bidders_count")
  private Integer biddersCount;

  @Column(name = "bids_count")
  private Integer bidsCount;

  @Column(name = "current_price")
  private Long currentPrice;

  @Column(name = "ceiling_price")
  private Long ceilingPrice;

  @Column(name = "winner_id")
  private Integer winnerId;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
