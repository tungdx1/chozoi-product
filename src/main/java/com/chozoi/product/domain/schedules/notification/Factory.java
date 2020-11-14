package com.chozoi.product.domain.schedules.notification;

import com.chozoi.product.domain.entities.postgres.types.SegmentState;
import com.chozoi.product.domain.schedules.notification.handle.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Factory {
  @Autowired private SendToWinner sendToWinner;
  @Autowired private SendStopped sendStopped;
  @Autowired private SendTimeRemain sendTimeRemain;
  @Autowired private SendOrderPending sendOrderPending;

  private Factory() {}

  public TypeHandle getTypeHandle(SegmentState state) {
    switch (state) {
      case WINER:
        return sendToWinner;
      case STOPPED:
        return sendStopped;
      case TIME_REMAIN:
        return sendTimeRemain;
      case ORDER_PENDING:
        return sendOrderPending;
      default:
        throw new IllegalArgumentException("Segment type unsupported processing");
    }
  }
}
