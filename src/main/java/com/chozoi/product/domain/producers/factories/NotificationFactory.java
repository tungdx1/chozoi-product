package com.chozoi.product.domain.producers.factories;

import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import com.chozoi.product.domain.entities.postgres.types.SegmentObjectType;
import com.chozoi.product.domain.entities.postgres.types.SegmentState;
import com.chozoi.product.domain.producers.NotificationMessageProducer;
import com.chozoi.product.domain.producers.content.AuctionData;
import com.chozoi.product.domain.producers.content.NotificationMessage;
import com.chozoi.product.domain.utils.JsonParser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class NotificationFactory implements KafkaFactory {
  @Autowired
  private NotificationMessageProducer producer;

  static AuctionData builderAuction(Product product) {
    LocalDateTime a = product.getAuction().getTimeEnd();
    long timeEnd = a.toEpochSecond(ZoneOffset.UTC);
    long nowSecond = System.currentTimeMillis() / 1000;
    long time = timeEnd - nowSecond;
    long remainTime = time < 0 ? 0 : time;
    AuctionData auctionData = new AuctionData();
    auctionData.setRemainTime(remainTime);
    auctionData.setCurrentPrice(product.getAuction().getResult().getCurrentPrice());
    auctionData.setBidPrice(product.getAuction().getResult().getCurrentPrice());
    auctionData.setBidCount((long) product.getAuction().getResult().getBidsCount());
    return auctionData;
  }

  @Override
  public void sendEvent(Product product, String segmentState, SegmentObjectType type, String reason)
          throws IOException {
    String userId = String.valueOf(product.getShop().getUserId());
    Map<String, String> data = new HashMap<>();
    data.put("id", String.valueOf(product.getId()));
    data.put("state", String.valueOf(segmentState));
    data.put("name", product.getName());
    String image = "";
    try {
      image = product.getImages().get(0).getImageUrl();
    } catch (Exception e) {
    }
    ;
    data.put("image", image);
    if ( type.equals(SegmentObjectType.AUCTION) ) data.put("auction", JsonParser.toJson(builderAuction(product)));
    NotificationMessage notificationMessage =
            NotificationMessage.builder()
                    .body(data)
                    .sendTo(userId)
                    .sendType("SELLER")
                    .type(String.valueOf(type))
                    .build();
    producer.sendMassage(notificationMessage);
  }

  @Override
  public void sendEventBuyer(Product product, String userId, SegmentState state) throws Exception {
    if ( !(product.getType().equals(ProductType.AUCTION)
            || product.getType().equals(ProductType.AUCTION_SALE)) ) return;

    Map<String, String> data = new HashMap<>();

    data.put("id", String.valueOf(product.getId()));
    data.put("state", String.valueOf(state));
    data.put("name", product.getName());
    try {
      data.put("image", product.getImages().get(0).getImageUrl());
    } catch (Exception e) {
      data.put("image", "");
    }
    data.put("auction", JsonParser.toJson(builderAuction(product)));
    NotificationMessage notificationMessage =
            NotificationMessage.builder()
                    .body(data)
                    .sendTo(userId)
                    .sendType("BUYER")
                    .type("AUCTION")
                    .build();
    producer.sendMassage(notificationMessage);
  }
}
