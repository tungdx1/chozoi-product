package com.chozoi.product.domain.producers.content;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuctionData implements Serializable {
  private Long bidCount;
  private Long bidPrice;
  private Long currentPrice;
  private Long remainTime;
  private Long pendingTime;
}
