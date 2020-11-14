package com.chozoi.product.domain.producers;

import chozoi.products.domain_event.Key;
import chozoi.products.domain_event.Value;
import com.chozoi.product.domain.producers.content.DomainEventSuggestion;
import com.chozoi.product.domain.producers.content.SuggestionEventContent;
import com.chozoi.product.domain.producers.types.EventTypeSuggestion;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Log4j2
public class SuggestionEventProducer {
  @Autowired
  private KafkaTemplate<Key, Value> producer;

  private Short version = 1;
  private String aggregate = "Log";

  public void sendMassage(SuggestionEventContent content, EventTypeSuggestion type) throws IOException {
    DomainEventSuggestion domainEvent = new DomainEventSuggestion();
    domainEvent.setId(UUID.randomUUID());
    domainEvent.setAggregate(aggregate);
    domainEvent.setType(type);
    domainEvent.setCreatedAt(LocalDateTime.now());
    domainEvent.setContent(content);
    domainEvent.setVersion(version);
    Value value = domainEvent.toValueAvro();
    producer.send(TopicConfig.TOPIC_SUGGESTION, value);
//    log.info("================== producer: " + value.getId());
  }


}
