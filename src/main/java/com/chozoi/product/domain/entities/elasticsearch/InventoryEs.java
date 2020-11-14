package com.chozoi.product.domain.entities.elasticsearch;

import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

@Data
public class InventoryEs {
  private Long id;
  private Long initialQuantity;
  private int inQuantity;
  private int outQuantity;
  private long remainingQuantity;

  public void inferProperties() {
    remainingQuantity = ObjectUtils.max(inQuantity - outQuantity, 0);
  }

  public void sync(InventoryEs inventory, int remainingQuantity) {
    this.id = inventory.id;
    this.initialQuantity = inventory.initialQuantity;
    this.inQuantity = inventory.inQuantity;
    this.outQuantity = inventory.inQuantity;
    this.remainingQuantity = remainingQuantity;
  }
}
