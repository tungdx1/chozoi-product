package com.chozoi.product.domain.entities.postgres.types;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum ProductType {
  NORMAL,
  AUCTION,
  AUCTION_SALE,
  AUCTION_FLASH_BID,
  CLASSIFIER,
  PROMOTION,
  SPECIAL,
}
