package com.chozoi.product.domain.entities.postgres;

import com.chozoi.product.domain.entities.postgres.types.ProductAuctionState;
import com.chozoi.product.domain.entities.postgres.types.ProductAuctionType;
import com.chozoi.product.domain.utils.PostgreSQLEnumType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "auction", schema = "auctions")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class Auction implements Cloneable {

  @Id private Long id;

  @OneToOne(mappedBy = "auction")
  private Product product;

  @Enumerated(value = EnumType.STRING)
  @Type(type = "pgsql_enum")
  @Column(nullable = false, columnDefinition = "auction_state", name = "state")
  private ProductAuctionState state;

  @Enumerated(value = EnumType.STRING)
  @Type(type = "pgsql_enum")
  @Column(nullable = false, columnDefinition = "auction_type", name = "type")
  private ProductAuctionType type;

  @Column(name = "time_start")
  private LocalDateTime timeStart;

  @Column(name = "time_end")
  private LocalDateTime timeEnd;

  @Column(name = "price_step")
  private long priceStep;

  @Column(name = "start_price")
  private long startPrice;

  @Column(name = "buy_now_price")
  private Long buyNowPrice;

  @Column(name = "original_price")
  private long originalPrice;

  @Column(name = "expected_price")
  private Long expectedPrice;

  @JsonProperty("expected_max_price")
  private Long expectedMaxPrice;

  @Column(name = "time_duration")
  private Integer timeDuration;

  @Column(name = "refuse_payment")
  private Boolean refusePayment;

  @Column(name = "phase_id")
  private Long phaseId;

  @OneToOne(mappedBy = "auction")
  @JoinColumn(name = "id", insertable = false, updatable = false)
  private AuctionResult result;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @SneakyThrows
  @Override
  public Auction clone() throws CloneNotSupportedException {
    try {
      return (Auction) super.clone();
    } catch (Exception e) {
      throw new Exception(e);
    }
  }
}
