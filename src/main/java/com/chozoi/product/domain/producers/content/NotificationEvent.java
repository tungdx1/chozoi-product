package com.chozoi.product.domain.producers.content;

import com.chozoi.event.message.NotificationMessageAuto;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class NotificationEvent {
  private String sendTo;
  private String type;
  private String sendType;
  private Map<String, String> body;

  public NotificationMessageAuto toValueAvro() {
    NotificationMessageAuto value = new NotificationMessageAuto();
    value.setType(getType());
    value.setSendTo(getSendTo());
    value.setSendType(getSendType());
    value.setBody(getBody());
    return value;
  }
}
