package com.chozoi.product.domain.producers.content;

import lombok.Data;

import java.util.Map;

@Data
public class NotificationBuyer {
  private String sendTo;
  private String type;
  private String sendType;
  private Map<String, String> body;
}
