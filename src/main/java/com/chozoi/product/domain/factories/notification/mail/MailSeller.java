package com.chozoi.product.domain.factories.notification.mail;

import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.User;
import com.chozoi.product.domain.entities.postgres.types.SegmentObjectType;
import com.chozoi.product.domain.entities.postgres.types.SegmentState;
import com.chozoi.product.domain.factories.notification.EventChozoi;
import com.chozoi.product.domain.factories.notification.MessageAbstract;
import com.chozoi.product.domain.producers.content.EmailContent;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
public class MailSeller extends MessageAbstract implements EventChozoi {
  @Override
  public void send(
      Product product,
      SegmentState segmentState,
      SegmentObjectType type,
      Map<String, Object> metaData)
      throws Exception {
    String reason = (String) metaData.getOrDefault("reason", "");
    User user =
        userRepository
            .findById(product.getShop().getUserId())
            .orElseThrow(() -> new Exception("user not found"));
    String userName = getUserName(user.getId());
    Map<String, String> data = new HashMap<>();
    data.put("userName", userName);
    data.put("id", String.valueOf(product.getId()));
    data.put("name", product.getName());
    String image = getImageProduct(product.getId());
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
    mailproducer.save(message);
//    log.info("------------->MailSeller send {}", data);
  }
}
