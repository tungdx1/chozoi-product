package com.chozoi.product.domain.factories.notification.mail;

import com.chozoi.product.domain.factories.notification.AbstractFactory;
import com.chozoi.product.domain.factories.notification.EventChozoi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailMessageFactory extends AbstractFactory {
  @Autowired private MailBuyer mailBuyer;
  @Autowired private MailSeller mailSeller;

  @Override
  public EventChozoi getEvent(String type) {
    if (type.equalsIgnoreCase("BUYER")) return mailBuyer;
    else if (type.equalsIgnoreCase("SELLER")) return mailSeller;
    return null;
  };
}
