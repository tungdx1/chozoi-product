package com.chozoi.product.domain.producers;

import chozoi.products.domain_event.Key;
import chozoi.products.domain_event.Value;
import com.chozoi.product.domain.entities.postgres.DomainEvent;
import com.chozoi.product.domain.entities.postgres.types.EventType;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class DomainEventProducer {

    @Autowired
    private KafkaTemplate<Key, Value> productMessageKafkaTemplate;

    public void save(DomainEvent event) {
        try {
            if (event.getType().equals(EventType.ProductViewed)) {
                this.productMessageKafkaTemplate.send(TopicConfig.TOPIC_VIEW, event.toValueAvro());
            } else {
                this.productMessageKafkaTemplate.send(TopicConfig.TOPIC, event.toValueAvro());
            }
        } catch (Exception e) {
            log.debug(e);
        }
    }
}
