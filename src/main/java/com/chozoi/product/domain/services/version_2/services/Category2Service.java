package com.chozoi.product.domain.services.version_2.services;

import com.chozoi.product.domain.entities.postgres.Category;
import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.services.design_patterns.database_factory.DatabaseFactory;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Category2Service {
  @Autowired private DatabaseFactory databaseFactory;

  /**
   * set category for product
   *
   * @param product
   */
  public void setCategory(Product product) throws Exception {
    Integer categoryId = ObjectUtils.defaultIfNull(product.getCategory().getId(), 0);
    Category category =
        (Category)
            databaseFactory.getFactory(DatabaseFactory.Type.POSTGRES).getCategory().get(categoryId);
    product.setCategory(category);
  }
}
