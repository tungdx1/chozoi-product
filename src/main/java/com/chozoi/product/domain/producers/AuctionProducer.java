package com.chozoi.product.domain.producers;

import lombok.extern.log4j.Log4j2;
import message.product.accept.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AuctionProducer {
  @Autowired private KafkaTemplate<Long, Value> productMessageKafkaTemplate;

  public void save(Value event) {
    try {
      this.productMessageKafkaTemplate.send(TopicConfig.TOPIC_PRODUCT_ACCEPT, event);
    } catch (Exception e) {
      log.debug(e);
    }
  }
}
