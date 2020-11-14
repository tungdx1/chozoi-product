package com.chozoi.product.domain.services.version_2.services;

import com.chozoi.product.domain.entities.postgres.Product;
import com.chozoi.product.domain.entities.postgres.Shop;
import com.chozoi.product.domain.services.design_patterns.database_factory.DatabaseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Shop2Service {
  @Autowired private DatabaseFactory databaseFactory;

  /**
   * Set shop for product
   *  @param product
   * @param shopId
   * @param userId
   */
  public void setShop(Product product, int shopId, Integer userId) throws Exception {
    Shop shop =
        (Shop) databaseFactory.getFactory(DatabaseFactory.Type.POSTGRES).getShop().getData(shopId);
    if (!shop.getUserId().equals(userId)) throw new Exception("Bạn không thuộc quyền quản lý cửa hàng!");
    product.setShop(shop);
  };
}
