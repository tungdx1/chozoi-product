package com.chozoi.product.domain.producers.content;

import com.chozoi.emailservice.domain.avro.MailMessage;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class EmailContent {
  private String emailTo;
  private String type;
  private String sendType;
  private Map<String, String> body;

  public MailMessage toValueAvro() {
    MailMessage mail = new MailMessage();
    mail.setBody(getBody());
    mail.setEmailTo(getEmailTo());
    mail.setType(getType());
    mail.setSendType(getSendType());
    return mail;
  }
}
