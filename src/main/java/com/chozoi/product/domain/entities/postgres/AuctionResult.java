package com.chozoi.product.domain.entities.postgres;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "auctions", name = "auction_result")
@Data
@Immutable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionResult {

  @Id private Long id;

  @OneToOne
  @JoinColumn(name = "id", insertable = false, updatable = false)
  @JsonIgnore
  @MapsId
  private Auction auction;

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

  @Transient private Long bidPrice;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
