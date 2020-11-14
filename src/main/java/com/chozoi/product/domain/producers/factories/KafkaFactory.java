package com.chozoi.product.domain.producers.factories;

import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.types.SegmentObjectType;
import com.chozoi.product.domain.entities.postgres.types.SegmentState;

public interface KafkaFactory {
  public void sendEvent(Product product, String state, SegmentObjectType type, String reason)
      throws Exception;

  public void sendEventBuyer(Product product, String userId, SegmentState state) throws Exception;
}
