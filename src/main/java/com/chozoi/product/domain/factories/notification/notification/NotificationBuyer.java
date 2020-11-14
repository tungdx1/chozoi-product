package com.chozoi.product.domain.factories.notification.notification;

import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import com.chozoi.product.domain.entities.postgres.types.SegmentObjectType;
import com.chozoi.product.domain.entities.postgres.types.SegmentState;
import com.chozoi.product.domain.factories.notification.EventChozoi;
import com.chozoi.product.domain.factories.notification.MessageAbstract;
import com.chozoi.product.domain.producers.content.AuctionData;
import com.chozoi.product.domain.producers.content.NotificationMessage;
import com.chozoi.product.domain.services.static_service.ProductStaticService;
import com.chozoi.product.domain.utils.JsonParser;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
public class NotificationBuyer extends MessageAbstract implements EventChozoi {
  @Override
  public void send(
      Product product,
      SegmentState segmentState,
      SegmentObjectType type,
      Map<String, Object> metaData)
      throws IOException {
    assert !(product.getType().equals(ProductType.AUCTION)
        || product.getType().equals(ProductType.AUCTION_SALE));
    String userId = (String) metaData.getOrDefault("userId", null);
    assert userId != null;
    Map<String, String> data = new HashMap<>();
    data.put("id", String.valueOf(product.getId()));
    data.put("state", String.valueOf(segmentState));
    data.put("name", product.getName());
    try {
      data.put("image", product.getImages().get(0).getImageUrl());
    } catch (Exception e) {
      data.put("image", "");
    }
    int pendingTime = ObjectUtils.defaultIfNull((Integer) metaData.get("pendingTime"), 0);
    int remainTime = ObjectUtils.defaultIfNull((Integer) metaData.get("timeRemain"), 0);
    data.put("pendingTime", String.valueOf(pendingTime));
    AuctionData auctionData = ProductStaticService.builderAuction(product);
    auctionData.setPendingTime((long) pendingTime);
    auctionData.setRemainTime((long) remainTime);
    data.put("auction", JsonParser.toJson(auctionData));
    NotificationMessage notificationMessage =
        NotificationMessage.builder()
            .body(data)
            .sendTo(userId)
            .sendType("BUYER")
            .type("AUCTION")
            .build();
    notificationProducer.sendMassage(notificationMessage);
  }
}
