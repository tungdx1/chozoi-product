package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.Category;
import com.chozoi.product.domain.entities.postgres.types.CategoryState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByIdAndState(Integer id, CategoryState state);

    Page<Category> findByStateOrderByLevel(CategoryState state, Pageable pageable);
}
