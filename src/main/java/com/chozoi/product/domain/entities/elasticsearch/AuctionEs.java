package com.chozoi.product.domain.entities.elasticsearch;

import com.chozoi.product.domain.entities.postgres.AuctionRessultFlashBid;
import com.chozoi.product.domain.entities.postgres.types.InstantBidType;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Log4j2
public class AuctionEs {
  private long id;
  private long buyNowPrice;
  private long originalPrice;
  private long priceStep;
  private long startPrice;
  private String state;
  private long timeDuration;
  private Object timeEnd;
  private AuctionResultEs result;
  private Object timeStart;
  private Object expectedPrice;
  private Object expectedMaxPrice;
  private long priceBid;
  private Long phaseId;
  private int lastMinuteBidCount;
  private long priceAutoBid;
  private InstantBidType typeBid = InstantBidType.MANUAL;
  private Boolean refusePayment;

  public void mappingTime() {
    if (timeStart instanceof ArrayList) {
      List<Integer> start = (List<Integer>) timeStart;
      List<Integer> end = (List<Integer>) timeEnd;
      this.timeStart =
          LocalDateTime.of(
              start.get(0), start.get(1), start.get(2), start.get(3), start.get(4), start.get(5));
      this.timeEnd =
          LocalDateTime.of(end.get(0), end.get(1), end.get(2), end.get(3), end.get(4), end.get(5));
    }
  }

  public void sync(AuctionEs auction, Long phaseId, InstantBidType typeBid, Long price, int lastMinuteBidCount, AuctionRessultFlashBid auctionRessultFlashBid, boolean refusePayment) {
    this.id = auction.id;
    this.buyNowPrice = auction.buyNowPrice;
    this.originalPrice = auction.originalPrice;
    this.priceStep = auction.priceStep;
    this.startPrice = auction.startPrice;
    this.state = auction.state;
    this.timeDuration = auction.timeDuration;
    this.timeEnd = auction.timeEnd;

    this.timeStart = auction.timeStart;
    this.expectedPrice = auction.expectedPrice;
    this.expectedMaxPrice = auction.expectedMaxPrice;
    this.priceBid = price;
    this.phaseId = phaseId;
    this.lastMinuteBidCount = lastMinuteBidCount;
    this.typeBid = typeBid;
    this.refusePayment = refusePayment;
    if (auctionRessultFlashBid != null) {
      AuctionResultEs resultEs = new AuctionResultEs();
      resultEs.sync(auctionRessultFlashBid);
      this.result = resultEs;
    } else {
      this.state = "BIDDING";
      this.result = auction.result;
    }
  }

  public void sync(AuctionEs auction, Long phaseId, InstantBidType typeBid, Long price, int lastMinuteBidCount, AuctionResultData auctionRessultFlashBid) {
    this.id = auction.id;
    this.buyNowPrice = auction.buyNowPrice;
    this.originalPrice = auction.originalPrice;
    this.priceStep = auction.priceStep;
    this.startPrice = auction.startPrice;
    this.state = auction.state;
    this.timeDuration = auction.timeDuration;
    this.timeEnd = auction.timeEnd;

    this.timeStart = auction.timeStart;
    this.expectedPrice = auction.expectedPrice;
    this.expectedMaxPrice = auction.expectedMaxPrice;
    this.priceBid = price;
    this.phaseId = phaseId;
    this.lastMinuteBidCount = lastMinuteBidCount;
    this.typeBid = typeBid;
    this.refusePayment = auction.refusePayment;
    if (auctionRessultFlashBid != null) {
      AuctionResultEs resultEs = new AuctionResultEs();
      resultEs.sync(auctionRessultFlashBid);
      this.result = resultEs;
    } else {
      this.result = auction.result;
    }
  }
  //  private Long createdAt;
  //  private Long updatedAt;
}
