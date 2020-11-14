package com.chozoi.product.domain.services.design_patterns.database_factory;

import com.chozoi.product.domain.services.design_patterns.database_factory.category.CategoryFactory;
import com.chozoi.product.domain.services.design_patterns.database_factory.config_home.ConfigHome;
import com.chozoi.product.domain.services.design_patterns.database_factory.shop.ShopFactory;

public abstract class DatabaseAbstractFactory {
    public abstract ConfigHome getConfig();

    public abstract ShopFactory getShop();

    public abstract CategoryFactory getCategory();
}
