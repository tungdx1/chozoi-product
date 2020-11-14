package com.chozoi.product.domain.producers.content;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductNotificationData {
  private Long id;
  private String type;
  private String state;
}
