package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.NotificationSegment;
import com.chozoi.product.domain.entities.postgres.types.SegmentObjectType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationSegmentRepository extends JpaRepository<NotificationSegment, Integer> {
  List<NotificationSegment> findByObjectTypeAndObjectValue(SegmentObjectType type, String value);

  List<NotificationSegment> findByObjectTypeAndObjectValueIn(
      SegmentObjectType type, List<String> value);
}
