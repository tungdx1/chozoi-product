package com.chozoi.product.domain.entities.elasticsearch;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;
import java.util.UUID;

@Data
@Document(indexName = "chozoi_auctions_result", type = "_doc")
public class AuctionResultData {
  @Id private UUID id;
  private long auctionId;
  private long biddersCount;
  private long bidsCount;
  private long currentPrice;
  private long ceilingPrice;
  private long winnerId;
  private long phaseId;
  private long createdAt;
  private long updatedAt;
}
