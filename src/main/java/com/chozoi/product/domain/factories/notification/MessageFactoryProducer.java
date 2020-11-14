package com.chozoi.product.domain.factories.notification;

import com.chozoi.product.domain.factories.notification.mail.MailMessageFactory;
import com.chozoi.product.domain.factories.notification.notification.NotificationMsgFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class MessageFactoryProducer {
  @Autowired private MailMessageFactory mailFactory;
  @Autowired private NotificationMsgFactory notificationFactory;

  public AbstractFactory getFactory(Type type) {
    if (type.equals(Type.MAIL)) return mailFactory;
    else if (type.equals(Type.NOTIFICATION)) return notificationFactory;
    return null;
  }

  public enum Type {
    MAIL,
    NOTIFICATION
  }
}
