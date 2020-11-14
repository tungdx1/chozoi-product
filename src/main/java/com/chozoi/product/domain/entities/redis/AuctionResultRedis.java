package com.chozoi.product.domain.entities.redis;

import com.chozoi.product.domain.entities.abstracts.AuctionResult;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

@Data
@Builder
@RedisHash(value = "ProductAuctionResult", timeToLive = 60)
public class AuctionResultRedis extends AuctionResult {
  @Id private Long id;
  private Integer biddersCount;
  private Integer bidsCount;
  private Long currentPrice;
  private Long ceilingPrice;
}
