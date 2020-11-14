package com.chozoi.product.domain.producers;

import com.bussinesslog.analytics.domain.models.LogMessage;
import lombok.extern.log4j.Log4j2;
import message.product.accept.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class ProductLogProducer {
  @Autowired private KafkaTemplate<Long, LogMessage> productMessageKafkaTemplate;

  public void save(LogMessage event) {
    try {
      this.productMessageKafkaTemplate.send(TopicConfig.TOPIC_PRODUCT_LOG, event);
    } catch (Exception e) {
      log.debug(e);
    }
  }
}
