package com.chozoi.product.domain.entities.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;
import java.util.UUID;

@Document(indexName = "chozoi_auctions_participant", type = "_doc")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionParticipantEs implements Comparable<AuctionParticipantEs> {
  @Id private UUID id;
  private Long auctionId;
  private Integer userId;
  private Long instantBidId;
  private Long autoBidId;
  private Long lastMinuteBidId;
  private String state;
  private int sort;
  private Long phaseId;
  private Long createdAt;
  private Long updatedAt;

  @Override
  public int compareTo(AuctionParticipantEs u) {
    if (getCreatedAt() == null || u.getCreatedAt() == null) return 0;
    return getCreatedAt().compareTo(u.getCreatedAt());
  }
}
