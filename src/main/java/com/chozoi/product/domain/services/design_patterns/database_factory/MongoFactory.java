package com.chozoi.product.domain.services.design_patterns.database_factory;

import com.chozoi.product.domain.services.design_patterns.database_factory.category.CategoryFactory;
import com.chozoi.product.domain.services.design_patterns.database_factory.config_home.ConfigHomeMongo;
import com.chozoi.product.domain.services.design_patterns.database_factory.shop.ShopFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MongoFactory extends DatabaseAbstractFactory {
    @Autowired
    ConfigHomeMongo configHomeMongo;

    @Override
    public ConfigHomeMongo getConfig() {
        return configHomeMongo;
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
