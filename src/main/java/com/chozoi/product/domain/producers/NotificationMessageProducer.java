package com.chozoi.product.domain.producers;

import com.chozoi.event.message.NotificationMessageAuto;
import com.chozoi.product.domain.producers.content.NotificationMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class NotificationMessageProducer {
  @Autowired
  private KafkaTemplate<String, NotificationMessageAuto> producer;

  private Short version = 1;
  private String aggregate = "Log";

  public void sendMassage(NotificationMessage content) {
    try {
      this.producer.send(TopicConfig.TOPIC_NOTIFICATION, content.toValueAvro());
    } catch (Exception e) {
      log.debug(e);
    }
  }
}