package com.chozoi.product.domain.consumers;

import io.sentry.Sentry;
import lombok.extern.log4j.Log4j2;
import org.springframework.retry.annotation.Recover;

@Log4j2
public class BaseProcessor {
  @Recover
  public void connectionException(Exception e) {

    log.error("==========================Retry done===========================: " + e.getMessage());
    Sentry.capture("Retry: " + e.getMessage());
  }
}
