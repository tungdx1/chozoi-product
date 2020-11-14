package com.chozoi.product.domain.producers;

import com.chozoi.emailservice.domain.avro.MailMessage;
import com.chozoi.product.domain.producers.content.EmailContent;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Log4j2
@Component
public class MailProducer {
  @Autowired
  private KafkaTemplate<String, MailMessage> productMessageKafkaTemplate;

  public void save(EmailContent event) {
    try {
      this.productMessageKafkaTemplate.send(TopicConfig.TOPIC_EMAIL, event.toValueAvro());
    } catch (Exception e) {
      log.debug(e);
    }
  }
}
