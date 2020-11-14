package com.chozoi.product.domain.services.design_patterns.database_factory.shop;

import com.chozoi.product.domain.entities.abstracts.Shop;

public interface ShopFactory {
  Shop getData(Integer shopId) throws Exception;
}
