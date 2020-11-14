package com.chozoi.product.domain.services;

import com.chozoi.product.domain.entities.postgres.Category;
import com.chozoi.product.domain.entities.postgres.HomeCategory;
import com.chozoi.product.domain.exceptions.CategoryNotFoundException;
import com.chozoi.product.domain.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j

public class CategoryService extends BaseService {

    /**
     * Get categories by page
     *
     * @return page of category
     */
    public Page<HomeCategory> findAll(Integer level, Integer parentId, Pageable pageable) {
        if (ObjectUtils.allNotNull(level, parentId)) {
            return categoryHomeRepository.findByLevelAndParentId(level, parentId, pageable);
        } else if (Objects.nonNull(level)) {
            return categoryHomeRepository.findByLevel(level, pageable);
        } else if (Objects.nonNull(parentId)) {
            return categoryHomeRepository.findByParentId(parentId, pageable);
        } else {
            return categoryHomeRepository.findAll(pageable);
        }
    }

    /**
     * Get category by id
     *
     * @return category
     */
    public Category findById(Integer categoryId) throws ResourceNotFoundException {
        return categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Categories not found"));
    }
}
