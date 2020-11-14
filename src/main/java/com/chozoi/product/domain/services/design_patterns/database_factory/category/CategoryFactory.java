package com.chozoi.product.domain.services.design_patterns.database_factory.category;

import com.chozoi.product.domain.entities.abstracts.Category;

public interface CategoryFactory {
    Category get(Integer integer) throws Exception;
}
