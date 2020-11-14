package com.chozoi.product.domain.exceptions;

import io.sentry.Sentry;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;

import java.util.List;

@Log4j2
public class EnhancedSeekToCurrentErrorHandler extends SeekToCurrentErrorHandler {

  public EnhancedSeekToCurrentErrorHandler(int maxFailures) {
    super(maxFailures);
  }

  @Override
  public void handle(
      Exception thrownException,
      List<ConsumerRecord<?, ?>> records,
      Consumer<?, ?> consumer,
      MessageListenerContainer container) {
    try {
      Sentry.capture(thrownException);
    } catch (Exception e) {
      log.error("================sentry exception: " + e);
    }

    log.error("================EnhancedSeekToCurrentErrorHandler: " + thrownException);

    super.handle(thrownException, records, consumer, container);
  }
}
