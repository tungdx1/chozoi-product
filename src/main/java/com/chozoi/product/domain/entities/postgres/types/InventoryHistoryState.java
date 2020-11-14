package com.chozoi.product.domain.entities.postgres.types;

public enum InventoryHistoryState {
  INITIALIZED,
  ADDED,
  ORDERED,
  CANCELED,
  RESERVED,
  SUBTRACTED
}
