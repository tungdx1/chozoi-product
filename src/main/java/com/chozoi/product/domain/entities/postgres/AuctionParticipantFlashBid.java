package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.entities.postgres.primary_key.AuctionParticipantFlashBidId;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auction_participant_flash_bid", schema = "auctions")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
@Data
@NoArgsConstructor
public class AuctionParticipantFlashBid {
  @EmbeddedId private AuctionParticipantFlashBidId id;

  @Column(name = "instant_bid_id")
  private Long instantBidId;

  @Column(name = "auto_bid_id")
  private Long autoBidId;

  @Column(name = "last_minute_bid_id")
  private Long lastMinuteBidId;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "auction_participant_state", nullable = false)
  @Type(type = "pg-enum")
  private State state;

  @Column(name = "last_minute_bid_count")
  private Integer lastMinuteBidCount;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  public static enum State {
    NORMAL,
    HIDDEN,
    DELETED
  }
}
