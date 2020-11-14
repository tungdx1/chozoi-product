package com.chozoi.product.domain.entities.mongodb;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Document(value = "logs.handle.notification")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationHandle {
  @Id private String key;
  private Integer userId;
  private Long createdAt;
  private Long updatedAt;

  public static NotificationHandle init(String key) {
    return NotificationHandle.builder().key(key).createdAt(System.currentTimeMillis()).build();
  }
}
