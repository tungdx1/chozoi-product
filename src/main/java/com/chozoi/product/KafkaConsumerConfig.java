package com.chozoi.product;

import com.chozoi.product.domain.exceptions.EnhancedSeekToCurrentErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.retry.annotation.EnableRetry;

import java.util.Map;

@Configuration
@EnableRetry
@Slf4j
public class KafkaConsumerConfig {
  @Bean
  public ConcurrentKafkaListenerContainerFactory<SpecificRecord, SpecificRecord>
      kafkaListenerContainerFactory(
          ConsumerFactory<SpecificRecord, SpecificRecord> consumerFactory) {

    ConcurrentKafkaListenerContainerFactory<SpecificRecord, SpecificRecord> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.getContainerProperties().setAckOnError(false);
    factory.setErrorHandler(new EnhancedSeekToCurrentErrorHandler(500));
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);

    return factory;
  }

  @Bean
  public ConsumerFactory<SpecificRecord, SpecificRecord> consumerFactory(
      KafkaProperties properties) {
    Map<String, Object> consumerProperties = properties.buildConsumerProperties();
    log.info("Consumer properties: " + consumerProperties);

    return new DefaultKafkaConsumerFactory<>(consumerProperties);
  }
}
