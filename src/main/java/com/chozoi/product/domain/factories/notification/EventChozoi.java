package com.chozoi.product.domain.factories.notification;

import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.types.SegmentObjectType;
import com.chozoi.product.domain.entities.postgres.types.SegmentState;

import java.util.Map;

public interface EventChozoi {
  void send(
      Product product,
      SegmentState segmentState,
      SegmentObjectType type,
      Map<String, Object> metaData)
      throws Exception;
}
