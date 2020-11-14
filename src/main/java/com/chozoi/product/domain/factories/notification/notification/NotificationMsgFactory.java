package com.chozoi.product.domain.factories.notification.notification;

import com.chozoi.product.domain.factories.notification.AbstractFactory;
import com.chozoi.product.domain.factories.notification.EventChozoi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationMsgFactory extends AbstractFactory {
  @Autowired private NotificationBuyer notificationBuyer;
  @Autowired private NotificationSeller notificationSeller;

  @Override
  public EventChozoi getEvent(String type) {
    if (type.equalsIgnoreCase("BUYER")) return notificationBuyer;
    else if (type.equalsIgnoreCase("SELLER")) return notificationSeller;
    return null;
  };
}
