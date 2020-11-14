package com.chozoi.product.domain.factories.notification.mail;

import com.chozoi.product.domain.entities.elasticsearch.InstantBidEs;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.User;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import com.chozoi.product.domain.entities.postgres.types.SegmentObjectType;
import com.chozoi.product.domain.entities.postgres.types.SegmentState;
import com.chozoi.product.domain.factories.notification.EventChozoi;
import com.chozoi.product.domain.factories.notification.MessageAbstract;
import com.chozoi.product.domain.producers.content.AuctionData;
import com.chozoi.product.domain.producers.content.EmailContent;
import com.chozoi.product.domain.utils.JsonParser;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class MailBuyer extends MessageAbstract implements EventChozoi {
  @Override
  public void send(
      Product product,
      SegmentState segmentState,
      SegmentObjectType type,
      Map<String, Object> metaData)
      throws Exception {
    if (!(product.getType().equals(ProductType.AUCTION)
        || product.getType().equals(ProductType.AUCTION_SALE))) return;
    Object userob = metaData.get("userId");
    int userId = Integer.parseInt(String.valueOf(userob));
    User user = userRepository.findById(userId).orElseThrow(() -> new Exception("user not found"));
    String userName = getUserName(user.getId());
    Map<String, String> data = new HashMap<>();
    data.put("userName", userName);
    data.put("id", String.valueOf(product.getId()));
    data.put("state", String.valueOf(segmentState));
    data.put("name", product.getName());
    String image = getImageProduct(product.getId());
    data.put("image", image);
    data.put("reason", "");
    int pendingTime = ObjectUtils.defaultIfNull((Integer) metaData.get("pendingTime"), 0);
    int remainTime = ObjectUtils.defaultIfNull((Integer) metaData.get("timeRemain"), 0);
    product.getAuction().getResult().getBidPrice();
    Long bidPrice =
        Objects.isNull(product.getAuction().getResult().getBidPrice())
            ? getBidPrice(String.valueOf(userId), product.getId())
            : product.getAuction().getResult().getBidPrice();
    AuctionData auctionData = new AuctionData();
    auctionData.setRemainTime((long) (remainTime * 60));
    auctionData.setCurrentPrice(product.getAuction().getResult().getCurrentPrice());
    auctionData.setBidPrice(bidPrice);
    auctionData.setBidCount((long) product.getAuction().getResult().getBidsCount());
    auctionData.setPendingTime((long) pendingTime);
    data.put("auction", JsonParser.toJson(auctionData));

    EmailContent notificationMessage =
        EmailContent.builder()
            .body(data)
            .emailTo(user.getEmail())
            .sendType("BUYER")
            .type("AUCTION")
            .build();

    mailproducer.save(notificationMessage);
  }

  private Long getBidPrice(String userId, Long id) throws Exception {
    List<InstantBidEs> instantBidEs =
        auctionInstantBidRepository.findByUserIdAndAuctionIdOrderByCreatedAtDesc(
            Integer.valueOf(userId), id);
    if (instantBidEs.isEmpty()) return 0L;
    return instantBidEs.get(0).getPrice();
  }
}
