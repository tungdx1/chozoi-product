package com.chozoi.product.domain.services.elasticsearch;

import com.chozoi.product.domain.entities.elasticsearch.CategoriesEs;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CategoryElasticService extends BaseElasticService {
    public Page<CategoriesEs> findAll(Integer level, Integer parentId, Pageable pageable) {
        if (ObjectUtils.allNotNull(level, parentId)) {
            return categoriesRepository.findByLevelAndParentIdAndState(
                    level, parentId, "PUBLIC", pageable);
        } else if (Objects.nonNull(level)) {
            return categoriesRepository.findByLevelAndState(level, "PUBLIC", pageable);
        } else if (Objects.nonNull(parentId)) {
            return categoriesRepository.findByParentIdAndState(parentId, "PUBLIC", pageable);
        } else {
            return categoriesRepository.findByState("PUBLIC", pageable);
        }
    }
}
