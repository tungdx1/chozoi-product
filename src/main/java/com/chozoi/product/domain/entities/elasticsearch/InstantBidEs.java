package com.chozoi.product.domain.entities.elasticsearch;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;

@Data
@Document(indexName = "chozoi_auction_instant_bid", type = "_doc")
public class InstantBidEs {
  @Id
  private Long id;
  private Long auctionId;
  private Integer userId;
  private Long price;
  private String type;
  private int lastMinuteBidCount;
  private Long createdAt;
  private Long phaseId;
}
