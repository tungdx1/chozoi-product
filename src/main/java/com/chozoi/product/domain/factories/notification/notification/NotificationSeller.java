package com.chozoi.product.domain.factories.notification.notification;

import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.types.SegmentObjectType;
import com.chozoi.product.domain.entities.postgres.types.SegmentState;
import com.chozoi.product.domain.factories.notification.EventChozoi;
import com.chozoi.product.domain.factories.notification.MessageAbstract;
import com.chozoi.product.domain.producers.content.NotificationMessage;
import com.chozoi.product.domain.services.static_service.ProductStaticService;
import com.chozoi.product.domain.utils.JsonParser;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationSeller extends MessageAbstract implements EventChozoi {
  @Override
  public void send(
      Product product,
      SegmentState segmentState,
      SegmentObjectType type,
      Map<String, Object> metaData)
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
    if (type.equals(SegmentObjectType.AUCTION))
        data.put("auction", JsonParser.toJson(ProductStaticService.builderAuction(product)));
    NotificationMessage notificationMessage =
        NotificationMessage.builder()
            .body(data)
            .sendTo(userId)
            .sendType("SELLER")
            .type(String.valueOf(type))
            .build();
    notificationProducer.sendMassage(notificationMessage);
  }
}
