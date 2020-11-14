package com.chozoi.product.domain.entities.elasticsearch;

import com.chozoi.product.domain.entities.postgres.AuctionRessultFlashBid;
import lombok.Data;

@Data
public class AuctionResultEs {
  private long id;
  private int biddersCount;
  private int bidsCount;
  private long currentPrice;
  private long ceilingPrice;
  private int winnerId;

    public void sync(AuctionRessultFlashBid auctionRessultFlashBid) {
      this.id = auctionRessultFlashBid.getId().getId();
      this.biddersCount = auctionRessultFlashBid.getBiddersCount();
      this.bidsCount = auctionRessultFlashBid.getBidsCount();
      if (auctionRessultFlashBid.getCurrentPrice() != null)
        this.currentPrice = auctionRessultFlashBid.getCurrentPrice();
      if (auctionRessultFlashBid.getCeilingPrice() != null)
        this.ceilingPrice = auctionRessultFlashBid.getCeilingPrice();
      this.winnerId = auctionRessultFlashBid.getWinnerId();
    }

  public void sync(AuctionResultData auctionResultData) {
    this.id = auctionResultData.getAuctionId();
    this.biddersCount = (int) auctionResultData.getBiddersCount();
    this.bidsCount = (int) auctionResultData.getBidsCount();
    this.currentPrice = auctionResultData.getCurrentPrice();
    this.ceilingPrice = auctionResultData.getCeilingPrice();
    this.winnerId = (int) auctionResultData.getWinnerId();
  }
}
