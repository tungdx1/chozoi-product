package com.chozoi.product.domain.services.design_patterns.database_factory;

import com.chozoi.product.domain.services.design_patterns.database_factory.category.CategoryFactory;
import com.chozoi.product.domain.services.design_patterns.database_factory.config_home.ConfigHomeRedis;
import com.chozoi.product.domain.services.design_patterns.database_factory.shop.ShopFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RedisFactory extends DatabaseAbstractFactory {

  @Autowired ConfigHomeRedis configHomeRedis;

  @Override
  public ConfigHomeRedis getConfig() {
    return null;
  }

  @Override
  public ShopFactory getShop() {
    return null;
  }

  @Override
  public CategoryFactory getCategory() {
    return null;
  }
}
