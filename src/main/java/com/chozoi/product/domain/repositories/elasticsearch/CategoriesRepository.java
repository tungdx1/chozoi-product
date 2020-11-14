package com.chozoi.product.domain.repositories.elasticsearch;

import com.chozoi.product.domain.entities.elasticsearch.CategoriesEs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface CategoriesRepository
        extends ElasticsearchRepository<CategoriesEs, Integer> {
    List<CategoriesEs> findCategoriesByStateAndIdInAndLevelOrderBySortAsc(
            String state, List ids, int level);

    List<CategoriesEs> findCategoriesByStateAndIdInOrderBySortAsc(String state, List ids);

    Page<CategoriesEs> findByLevelAndParentIdAndState(
            Integer level, Integer parentId, String state, Pageable pageable);

    Page<CategoriesEs> findByLevelAndState(Integer level, String state, Pageable pageable);

    Page<CategoriesEs> findByParentIdAndState(
            Integer parentId, String state, Pageable pageable);

    Page<CategoriesEs> findByState(String aPublic, Pageable pageable);
}
