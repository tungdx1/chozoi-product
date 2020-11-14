package com.chozoi.product.domain.services.version_2.services;

import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.ProductStats;
import org.springframework.stereotype.Service;

@Service
public class ProductStatService {
  /**
   * set new stats for product
   *
   * @param product
   */
  public void setStats(Product product) {
    ProductStats stats = new ProductStats();
    product.setStats(stats);
  };
}
