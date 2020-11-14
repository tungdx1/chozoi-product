package com.chozoi.product.domain.producers.factories;

import com.chozoi.product.domain.entities.elasticsearch.ImageEs;
import com.chozoi.product.domain.entities.elasticsearch.InstantBidEs;
import com.chozoi.product.domain.entities.elasticsearch.ProductEs;
import com.chozoi.product.domain.entities.elasticsearch.ProfileEs;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.ProductImage;
import com.chozoi.product.domain.entities.postgres.User;
import com.chozoi.product.domain.entities.postgres.types.ProductType;
import com.chozoi.product.domain.entities.postgres.types.SegmentObjectType;
import com.chozoi.product.domain.entities.postgres.types.SegmentState;
import com.chozoi.product.domain.producers.MailProducer;
import com.chozoi.product.domain.producers.content.AuctionData;
import com.chozoi.product.domain.producers.content.EmailContent;
import com.chozoi.product.domain.repositories.elasticsearch.AuctionInstantBidRepository;
import com.chozoi.product.domain.repositories.elasticsearch.ProductEsRepository;
import com.chozoi.product.domain.repositories.elasticsearch.ProfileRepository;
import com.chozoi.product.domain.repositories.postgres.ProductReportIssueRepository;
import com.chozoi.product.domain.repositories.postgres.UserRepository;
import com.chozoi.product.domain.utils.JsonParser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Component
@Log4j2
public class MailFactory implements KafkaFactory {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ProfileRepository profileRepository;
  @Autowired
  private ProductEsRepository productEsRepository;
  @Autowired
  private AuctionInstantBidRepository auctionInstantBidRepository;
  @Autowired
  private ProductReportIssueRepository reportIssueRepository;
  @Autowired
  private MailProducer producer;

  @Override
  public void sendEvent(Product product, String segmentState, SegmentObjectType type, String reason)
          throws Exception {
    reason = Objects.isNull(reason) ? "" : reason;
    User user =
            userRepository
                    .findById(product.getShop().getUserId())
                    .orElseThrow(() -> new Exception("user not found"));

    String userName = getUserName(user.getId());
    Map<String, String> data = new HashMap<>();
    data.put("userName", userName);
    data.put("id", String.valueOf(product.getId()));
    data.put("name", product.getName());
    String image = "";
    List<ProductImage> imageList = product.getImages();
    if (imageList != null) {
      imageList.sort(Comparator.comparingInt(ProductImage::getSort));
      image = imageList.get(0).getImageUrl();
    }
    data.put("image", image);
    data.put("state", String.valueOf(segmentState));
    data.put("reason", reason);
    EmailContent message =
            EmailContent.builder()
                    .body(data)
                    .emailTo(user.getEmail())
                    .sendType("SELLER")
                    .type(String.valueOf(type))
                    .build();
    producer.save(message);
//    log.info("------------->MailFactory sendEvent {}", data);
  }

  private String getUserName(Integer id) {
    ProfileEs profileEs = profileRepository.findById(id).orElse(new ProfileEs());
    return profileEs.getName();
  }

  @Override
  public void sendEventBuyer(Product product, String userId, SegmentState state) throws Exception {
    if ( !(product.getType().equals(ProductType.AUCTION)
            || product.getType().equals(ProductType.AUCTION_SALE)) ) return;
    User user =
            userRepository
                    .findById(Integer.valueOf(userId))
                    .orElseThrow(() -> new Exception("user not found"));
    String userName = getUserName(user.getId());
    Map<String, String> data = new HashMap<>();
    data.put("userName", userName);
    data.put("id", String.valueOf(product.getId()));
    data.put("state", String.valueOf(state));
    data.put("name", product.getName());
    String image = getImageProduct(product.getId());
    data.put("image", image);
    data.put("reason", "");
    product.getAuction().getResult().getBidPrice();
    Long bidPrice =
            Objects.isNull(product.getAuction().getResult().getBidPrice())
                    ? getBidPrice(userId, product.getId())
                    : product.getAuction().getResult().getBidPrice();
    LocalDateTime a = product.getAuction().getTimeEnd();
    long timeEnd = a.toEpochSecond(ZoneOffset.UTC);
    long nowSecond = System.currentTimeMillis() / 1000;
    long time = timeEnd - nowSecond;
    long remainTime = time < 0 ? 0 : time;
    AuctionData auctionData = new AuctionData();
    auctionData.setRemainTime(remainTime);
    auctionData.setCurrentPrice(product.getAuction().getResult().getCurrentPrice());
    auctionData.setBidPrice(bidPrice);
    auctionData.setBidCount((long) product.getAuction().getResult().getBidsCount());
    data.put("auction", JsonParser.toJson(auctionData));

    EmailContent notificationMessage =
            EmailContent.builder()
                    .body(data)
                    .emailTo(user.getEmail())
                    .sendType("BUYER")
                    .type("AUCTION")
                    .build();
    producer.save(notificationMessage);
  }

  private Long getBidPrice(String userId, Long id) throws Exception {
    List<InstantBidEs> instantBidEs =
            auctionInstantBidRepository.findByUserIdAndAuctionIdOrderByCreatedAtDesc(
                    Integer.valueOf(userId), id);
    if ( instantBidEs.isEmpty() ) return 0L;
    return instantBidEs.get(0).getPrice();
  }

  private String getImageProduct(Long id) {
    try {
      ProductEs productEs =
              productEsRepository.findById(id).orElseThrow(() -> new Exception("not found"));
//      log.info("============> ProductEs {}", productEs);
      return productEs.getImages().get(0).getImageUrl();
    } catch (Exception e) {
      return "";
    }
  }
}
