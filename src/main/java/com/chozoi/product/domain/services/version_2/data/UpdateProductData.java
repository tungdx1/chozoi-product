package com.chozoi.product.domain.services.version_2.data;

import com.chozoi.product.domain.entities.postgres.Product;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProductData {
  private Product product;
  private Product productOld;
}
