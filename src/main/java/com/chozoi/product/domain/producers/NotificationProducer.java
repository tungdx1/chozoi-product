package com.chozoi.product.domain.producers;

import chozoi.products.domain_event.Key;
import com.chozoi.event.message.NotificationMessageAuto;
import com.chozoi.product.domain.producers.content.NotificationEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class NotificationProducer {
  @Autowired private KafkaTemplate<Key, NotificationMessageAuto> productMessageKafkaTemplate;

  public void save(NotificationEvent event) {
    try {
      this.productMessageKafkaTemplate.send(TopicConfig.TOPIC_NOTIFICATION, event.toValueAvro());
    } catch (Exception e) {
      log.debug(e);
    }
  }
}
