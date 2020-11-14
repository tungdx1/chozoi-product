package com.chozoi.product.domain.services.design_patterns.database_factory.category;

import com.chozoi.product.domain.entities.abstracts.Category;
import com.chozoi.product.domain.exceptions.ExceptionMessage;
import com.chozoi.product.domain.repositories.postgres.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryPostgresData implements CategoryFactory {
    @Autowired
    protected CategoryRepository categoryRepository;

    @Override
    public Category get(Integer id) throws Exception {
        return categoryRepository.findById(id).orElseThrow(() -> new Exception(ExceptionMessage.categoryNotFound(id)));
    }
}
