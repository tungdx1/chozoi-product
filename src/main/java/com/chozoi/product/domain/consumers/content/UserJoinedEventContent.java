package com.chozoi.product.domain.consumers.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJoinedEventContent {

  private long auctionId;

  private int userId;

  private long price;

  private long serverTime;

  private long processTime;

  private String type;
}
