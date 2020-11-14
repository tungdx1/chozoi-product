package com.chozoi.product.domain.producers;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicConfig {

  public static final String TOPIC = "chozoi.products.domain_event";
  public static final String TOPIC_VIEW = "chozoi.products.view_event";
  public static final String TOPIC_LOG = "chozoi.products.log_event";
  public static final String TOPIC_SUGGESTION = "chozoi.buyer.product.suggestion";
  public static final String TOPIC_NOTIFICATION = "event.message.notification.auto";
  public static final String TOPIC_EMAIL = "event.message.email";
  public static final String TOPIC_PRODUCT_ACCEPT = "message.product.accept";
  public static final String TOPIC_PRODUCT_FAVORITE = "chozoi.passport.favorite";
  public static final String TOPIC_PRODUCT_LOG = "chozoi.prelog";

  @Value("${spring.kafka.topic.num-partitions}")
  private int numPartitions;

  @Value("${spring.kafka.topic.replication-factor}")
  private short replicationFactor;

  @Bean
  public NewTopic productTopic() {
    return new NewTopic(TOPIC, 1, replicationFactor);
  }

  @Bean
  public NewTopic viewTopic() {
    return new NewTopic(TOPIC_VIEW, numPartitions, replicationFactor);
  }

  @Bean
  public NewTopic logTopic() {
    return new NewTopic(TOPIC_LOG, numPartitions, replicationFactor);
  }

  @Bean
  public NewTopic suggestTopic() {
    return new NewTopic(TOPIC_SUGGESTION, numPartitions, replicationFactor);
  }

  @Bean
  public NewTopic notificationTopic() {
    return new NewTopic(TOPIC_NOTIFICATION, numPartitions, replicationFactor);
  }

  @Bean
  public NewTopic emailTopic() {
    return new NewTopic(TOPIC_EMAIL, numPartitions, replicationFactor);
  }

  @Bean
  public NewTopic acceptProduct() {
    return new NewTopic(TOPIC_PRODUCT_ACCEPT, numPartitions, replicationFactor);
  }
}
