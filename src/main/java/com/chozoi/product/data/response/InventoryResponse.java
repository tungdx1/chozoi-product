package com.chozoi.product.data.response;

import com.chozoi.product.domain.entities.abstracts.InventoryAbs;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;

@Data
@Log4j2
public class InventoryResponse extends InventoryAbs {
  private Long id;
  private int outQuantity;
  private int inQuantity;
  private int initialQuantity;
  private int remainingQuantity;

  public void inferProperties() {
    remainingQuantity = ObjectUtils.max(inQuantity - outQuantity, 0);
  }

}
