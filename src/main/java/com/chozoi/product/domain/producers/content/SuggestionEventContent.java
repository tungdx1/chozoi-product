package com.chozoi.product.domain.producers.content;

import com.chozoi.product.domain.entities.postgres.EventContent;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class SuggestionEventContent extends EventContent {
  private String userId;
  private Integer shopId;
  private String deviceId;
  private String productId;
  private String channel;
  private String env;
  private String os;
  private String browser;
  private String ip;
  private String location;
  private Integer rating;
}

