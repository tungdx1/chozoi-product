package com.chozoi.product.domain.entities.mongodb;

import com.chozoi.product.domain.entities.abstracts.AuctionResult;
import com.chozoi.product.domain.entities.postgres.AuctionOnly;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZoneId;

@Document(collection = "auctions.auction")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionMongo {

  private Long id;

  private String state;

  private Long timeStart;

  private Long timeEnd;

  private Long priceStep;

  private Long startPrice;
  private Long phaseId;

  private Long originalPrice;

  private Long buyNowPrice;

  private Integer timeDuration;

  private AuctionResult result;

  private Long createdAt;

  private Long updatedAt;

  public void asignFrom(AuctionOnly auctionOnly) {
    ZoneId zt = ZoneId.of("GMT");
    this.id = auctionOnly.getId();
    this.state = auctionOnly.getState().toString();
    this.timeStart = auctionOnly.getTimeStart().atZone(zt).toInstant().toEpochMilli();
    this.timeEnd = auctionOnly.getTimeEnd().atZone(zt).toInstant().toEpochMilli();
    this.priceStep = auctionOnly.getPriceStep();
    this.startPrice = auctionOnly.getStartPrice();
    this.phaseId = auctionOnly.getPhaseId();
    this.originalPrice = auctionOnly.getOriginalPrice();
    this.buyNowPrice = auctionOnly.getBuyNowPrice();
    this.timeDuration = auctionOnly.getTimeDuration();
    this.result = AuctionResultMongo.create(id);
    this.createdAt = auctionOnly.getCreatedAt().atZone(zt).toInstant().toEpochMilli();
    this.updatedAt = auctionOnly.getUpdatedAt().atZone(zt).toInstant().toEpochMilli();
  }
}
