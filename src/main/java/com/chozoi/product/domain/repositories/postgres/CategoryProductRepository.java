package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.Category;
import com.chozoi.product.domain.entities.postgres.CategoryProduct;
import com.chozoi.product.domain.entities.postgres.types.CategoryState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryProductRepository extends JpaRepository<Category, Integer> {
    Page<CategoryProduct> findByStateOrderBySortAsc(CategoryState state, Pageable pageable);

    Page<Category> findByStateAndParentIdIn(
            CategoryState state, List<Integer> ids, Pageable pageable);
}
