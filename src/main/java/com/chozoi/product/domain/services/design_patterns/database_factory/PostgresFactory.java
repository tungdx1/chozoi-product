package com.chozoi.product.domain.services.design_patterns.database_factory;

import com.chozoi.product.domain.services.design_patterns.database_factory.category.CategoryFactory;
import com.chozoi.product.domain.services.design_patterns.database_factory.category.CategoryPostgresData;
import com.chozoi.product.domain.services.design_patterns.database_factory.config_home.ConfigHome;
import com.chozoi.product.domain.services.design_patterns.database_factory.config_home.ConfigHomePostgres;
import com.chozoi.product.domain.services.design_patterns.database_factory.shop.ShopFactory;
import com.chozoi.product.domain.services.design_patterns.database_factory.shop.ShopPostgresData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostgresFactory extends DatabaseAbstractFactory {
    @Autowired
    private ConfigHomePostgres configHomePostgres;
    @Autowired
    private ShopPostgresData shopPostgresData;

    @Autowired
    private CategoryPostgresData categoryPostgresData;

    @Override
    public ConfigHome getConfig() {
        return configHomePostgres;
    }

    @Override
    public ShopFactory getShop() {
        return shopPostgresData;
    }

    @Override
    public CategoryFactory getCategory() {
        return categoryPostgresData;
    }
}
