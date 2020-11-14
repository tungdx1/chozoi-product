package com.chozoi.product.domain.entities.postgres;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auction_phase", schema = "auctions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuctionPhase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "auction_id")
  private Long auctionId;

  @Column(name = "count_phase")
  private Integer countPhase;

  @Column(name = "this_phase")
  private Integer thisPhase;

  @Column(name = "start_time")
  private LocalDateTime startTime;

  @Column(name = "end_time")
  private LocalDateTime endTime;

  @Column(name = "winner_id")
  private Integer winnerId;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
