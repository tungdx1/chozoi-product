package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.values.AuctionPaymentRefuseId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_refuse", schema = "auctions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuctionPaymentRefuse {
  @EmbeddedId
  private AuctionPaymentRefuseId id;

  @Column(name = "user_id")
  private Integer userId;

  @Column(name = "payment_refused")
  private Boolean paymentRefused;

  @Column(name = "refused_at")
  private LocalDateTime refusedAt;
}
