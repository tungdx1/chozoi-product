package com.chozoi.product.domain.schedules.notification.handle;

import com.chozoi.product.domain.entities.postgres.NotificationSegment;

import java.util.List;

public interface TypeHandle {
  void handle(List<NotificationSegment> segments) throws Exception;
}
