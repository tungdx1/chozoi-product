package com.chozoi.product.domain.entities.mongodb;

import com.chozoi.product.domain.entities.abstracts.AuctionResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Transient;
import java.time.LocalDateTime;

@Data
@Document(collection = "auction.result")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionResultMongo extends AuctionResult {
  public Long id;

  public Integer biddersCount;

  public Integer bidsCount;

  public Long currentPrice;

  public Long ceilingPrice;

  public Integer winnerId;

  @Transient public Long bidPrice;
  public LocalDateTime createdAt;

  public LocalDateTime updatedAt;

  public static AuctionResultMongo create(long id) {
    return AuctionResultMongo.builder()
        .id(id)
        .biddersCount(0)
        .bidsCount(0)
        .currentPrice(0L)
        .ceilingPrice(0L)
        .winnerId(0)
        .bidPrice(0L)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }
}
