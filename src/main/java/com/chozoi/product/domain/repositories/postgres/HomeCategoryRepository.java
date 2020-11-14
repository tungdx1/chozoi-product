package com.chozoi.product.domain.repositories.postgres;

import com.chozoi.product.domain.entities.postgres.HomeCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeCategoryRepository extends JpaRepository<HomeCategory, Integer> {

    Page<HomeCategory> findByLevelAndParentId(int level, int parentId, Pageable pageable);

    Page<HomeCategory> findByLevel(int level, Pageable pageable);

    Page<HomeCategory> findByParentId(int parentId, Pageable pageable);
}
