package com.chozoi.product.domain.consumers;

import com.chozoi.product.app.dtos.ProductStateDto;
import com.chozoi.product.domain.constants.ConfigRetry;
import com.chozoi.product.domain.entities.postgres.types.ProductState;
import com.chozoi.product.domain.services.InternalService;
import io.sentry.Sentry;
import lombok.extern.log4j.Log4j2;
import message.product.accept.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Log4j2
@Component
public class AcceptProductProcessor extends BaseProcessor {
  @Autowired private InternalService internalService;

  @Override
  @Recover
  public void connectionException(Exception e) {

    log.error("==========================Retry done===========================: " + e.getMessage());
    Sentry.capture("Retry: " + e.getMessage());
  }

  @KafkaListener(topics = "message.product.accept", groupId = "chozoi_handle_1")
  @Retryable(
      value = {Exception.class, ListenerExecutionFailedException.class},
      maxAttempts = ConfigRetry.MAX_ATTEMPTS,
      backoff = @Backoff(delay = ConfigRetry.DELAY_RETRY))
  @Transactional
  public void process(@Payload(required = false) Value value) throws Exception {
    log.info("============ start handle product id : ");
    if (Objects.isNull(value)) return;
    if (Objects.isNull(value.getId())) return;
    Long productId = value.getId();
    ProductStateDto productStateDto = new ProductStateDto();
    log.info("============ start handle product id : " + productId);
    productStateDto.setState(ProductState.READY);
    productStateDto.setProductId(productId);
    productStateDto.setUserSystemId(1);
    productStateDto.setSolution("");
    productStateDto.setDescription("");
    productStateDto.setUpdatedVersion(1);
    internalService.productState(productStateDto);
  }
}
